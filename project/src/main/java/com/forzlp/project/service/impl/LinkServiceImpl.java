package com.forzlp.project.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.forzlp.project.common.convention.errorcode.BaseErrorCode;
import com.forzlp.project.common.convention.excetion.ClientException;
import com.forzlp.project.common.convention.excetion.ServiceException;
import com.forzlp.project.dao.entity.Goto;
import com.forzlp.project.dao.entity.Link;
import com.forzlp.project.dao.entity.LinkStats;
import com.forzlp.project.dao.mapper.GotoMapper;
import com.forzlp.project.dao.mapper.LinkMapper;
import com.forzlp.project.dao.mapper.LinkStatsMapper;
import com.forzlp.project.dto.dto.*;
import com.forzlp.project.dto.req.LinkCreateReqDTO;
import com.forzlp.project.dto.req.LinkSearchReqDTO;
import com.forzlp.project.dto.req.LinkStatsReqDTO;
import com.forzlp.project.dto.resp.LinkCreateRespDTO;
import com.forzlp.project.dto.resp.LinkSearchRespDTO;
import com.forzlp.project.dto.resp.LinkStatsRespDTO;
import com.forzlp.project.service.LinkService;
import com.forzlp.project.service.LinkTitleService;
import com.forzlp.project.utils.*;
import com.github.pagehelper.PageHelper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.forzlp.project.common.constant.LinkConstant.IP_GO_REGION_URL;
import static com.forzlp.project.common.constant.LinkRedisConstant.*;
import static com.forzlp.project.common.constant.RedisConstant.SHORT_LINK_STATS_UIP_KEY;
import static com.forzlp.project.common.constant.RedisConstant.SHORT_LINK_STATS_UV_KEY;

/**
 * Author 70ash
 * Date 2024/3/2 13:56
 * Description:
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LinkServiceImpl implements LinkService {
    private final LinkMapper linkMapper;
    private final RBloomFilter shortUrlCreateCachePenetrationBloomFilter;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;
    private final GotoMapper gotoMapper;
    private final LinkStatsMapper linkStatsMapper;
    @Resource
    private LinkTitleService linkTitleService;


    @Value("${short-link.stats.ip}")
    private String gaoDeKey;
    @Value("${short-link.domain.default}")
    private String defaultDomain;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     *
     * @param requestParam 创建短链接参数
     * @return 创建短链接返回参数
     * 获取参数信息，生成六位数随机数，拼接获得完整短链接(协议+域名+短链接)
     * 入库，把完整短链接插入布隆过滤器，根据短链接和分组标识插入路由表，后续用户输入短链接由路由表获取分组标识查询原始链接
     */
    @Override
    public LinkCreateRespDTO saveLink(LinkCreateReqDTO requestParam){
        // 协议
        String protocol = URLParser.extractProtocol(requestParam.getOriginUrl());
        // 域名
        String domain = URLParser.extractDomain(requestParam.getOriginUrl());
        // 路径
        String path = generateSuffix(requestParam);
        // 获取完整短链接
        String fullShortUrl = defaultDomain + "/" + path;
        String gid = requestParam.getGid();
        // 获取网站标题和图标
        String originUrl = requestParam.getOriginUrl();
        String title;
        if (requestParam.getTitle() == null || requestParam.getTitle().equals("")) {
           title = linkTitleService.extractTitle(originUrl);
        }else {
            title = requestParam.getTitle();
        }
        String icon = linkTitleService.extractIconUrl(originUrl);
        LinkCreateRespDTO linkCreateRespDTO = LinkCreateRespDTO.builder()
                .gid(gid)
                .originUrl(requestParam.getOriginUrl())
                .fullShortUrl(fullShortUrl)
                .title(title)
                .uv(0)
                .pv(0)
                .uip(0)
                .validType(requestParam.getValidType())
                .validTime(requestParam.getValidTime())
                .build();
        // 短链接
        Link link = Link.builder()
                .gid(requestParam.getGid())
                .domain(domain)
                .originUrl(requestParam.getOriginUrl())
                .shortUrl(path)
                .fullShortUrl(fullShortUrl)
                .icon(icon)
                .title(title)
                .validType(requestParam.getValidType())
                .validTime(requestParam.getValidTime())
                .clickNum(0)
                .enableStatus(0)
                .build();
        Goto aGoto = Goto.builder()
                .gid(requestParam.getGid())
                .shortUri(path)
                .build();
        LinkStats linkStats = LinkStats.builder()
                .gid(gid)
                .shortUri(path)
                .build();
        try {
            System.out.println(1);
            linkMapper.insertLink(link);
            gotoMapper.insertGoto2(aGoto);
            linkStatsMapper.insertLinkStats(linkStats);
            // 加入到布隆过滤器
            // 存入redis作缓存预热,
            Long linkCacheValidTime = LinkUtil.getLinkCacheValidTime(requestParam.getValidTime());
            stringRedisTemplate.opsForValue().set(
                    String.format(LINK_GOTO_KEY, path),
                    requestParam.getOriginUrl(),
                    linkCacheValidTime, TimeUnit.MILLISECONDS);

            shortUrlCreateCachePenetrationBloomFilter.add(fullShortUrl);
        }catch (DuplicateKeyException ex) {
            log.warn("短链接频繁创建，请稍后再试");
            throw new ClientException(BaseErrorCode.URL_DUPLICATE_CREATE);
        }
        return linkCreateRespDTO;
    }

    @Override
    public List<LinkSearchRespDTO> pageShortLink(LinkSearchReqDTO requestParam) {
        PageHelper.startPage(requestParam.getPageNum(), requestParam.getPageSize());
        return linkMapper.pageLink(requestParam);
    }

    /**
     * @param shortUri 短链接
     * 查路由表获取分组标识，由分组标识和短链接查询原始短链接并跳转
     */
    //TODO 进行短链接统计
    @Override
    public void restore(String shortUri, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String fullShortUrl = defaultDomain + "/" + shortUri;
        // 从redis之中获取
        String originUrl = stringRedisTemplate.opsForValue().get(String.format(LINK_GOTO_KEY, shortUri));
        // 如果获取的原始链接不为空，那么就进行跳转
        if (StrUtil.isNotEmpty(originUrl)) {
            linkStats(shortUri,request, response);
            response.sendRedirect(originUrl);
            return;
        }
        // 查询布隆过滤器
        boolean contains = shortUrlCreateCachePenetrationBloomFilter.contains(fullShortUrl);
        if (!contains) {
            response.sendRedirect("/page/notfound");
            return;
        }
        // 如果判断是否是空uri，如果是，返回一个空值
        String LinkIsNull = stringRedisTemplate.opsForValue().get(String.format(LINK_GOTO_IS_NULL_KEY, shortUri));
        if (StrUtil.isNotEmpty(LinkIsNull)) {
            response.sendRedirect("/page/notfound");
            return;
        }
        // 对单个短链接加锁
        RLock lock = redissonClient.getLock(String.format(LOCK_LINK_GOTO_KEY, shortUri));
        lock.lock();
        // 使用try--finally保证锁的释放
        try {
            // 双重判定锁
            originUrl = stringRedisTemplate.opsForValue().get(String.format(LINK_GOTO_KEY, shortUri));
            // 如果获取的原始链接不为空，那么就进行跳转
            if (StrUtil.isNotEmpty(originUrl)) {
                linkStats(shortUri,request, response);
                response.sendRedirect(originUrl);
                return;
            }
            LinkIsNull = stringRedisTemplate.opsForValue().get(String.format(LINK_GOTO_IS_NULL_KEY, shortUri));
            if (StrUtil.isNotEmpty(LinkIsNull)) {
                response.sendRedirect("/page/notfound");
                return;
            }
            // 获取分组标识
            String gid = linkMapper.gotoByUri(shortUri);
            // 由分组标识和原始短链接进行跳转
            Link link = linkMapper.selectLinkByUriAndGid(shortUri, gid);
            if (link != null) {
                originUrl = link.getOriginUrl();
                stringRedisTemplate.opsForValue().set(String.format(LINK_GOTO_KEY, shortUri), originUrl, LinkUtil.getLinkCacheValidTime(link.getValidTime()), TimeUnit.MILLISECONDS);
                linkStats(shortUri,request, response);
                response.sendRedirect(originUrl);
            } else { // 如果数据库中不存在，缓存一个空值
                stringRedisTemplate.opsForValue().set(String.format(LOCK_LINK_GOTO_KEY, shortUri), "-", LINK_GOTO_NULL_TIME_KEY, TimeUnit.SECONDS);
                response.sendRedirect("/page/notfound");
            }
        }finally {
            lock.unlock();
        }
    }
    /** 短链接数据统计 **/
    @Override
    public LinkStatsRespDTO linkStats(LinkStatsReqDTO requestParam) {
        // 查询全部短链接统计数据
        List<LinkStats> linkStatsList = linkMapper.selectLinkStats(requestParam);
        Integer totalPv = 0;
        // Integer totalUv = 0;
        Integer totalUip = 0;
        // 获取uv, pv ,uip信息
        int[] hourly = new int[24];
        int[] weekly = new int[7];
        HashMap<String, Integer> browsers = new HashMap<>();
        HashMap<String, Integer> os = new HashMap<>();
        HashMap<String, Integer> province = new HashMap<>();
        for (LinkStats linkStats : linkStatsList) {
            totalPv += Optional.ofNullable(linkStats.getPv()).orElse(0);
            // totalUv += Optional.ofNullable(linkStats.getUv()).orElse(0);
            totalUip += Optional.ofNullable(linkStats.getUip()).orElse(0);
        }
        // pv,uv,uip
        // 近期访问数据

        //TODO

        Date createTime = linkMapper.selectLinkCTimeByShortUri(requestParam.getGid(),requestParam.getShort_url());


        Date now = new Date();
        long timeDiff = now.getTime() - createTime.getTime();
        List<LinkRecentStatsDTO> tempList;
        List<LinkRecentStatsDTO> list;
        if (TimeUnit.MILLISECONDS.toHours(timeDiff) >= 24) {
            // 返回近七天数据
            tempList = linkMapper.select7StatsHourly(requestParam.getShort_url());
            // 返回二十四小时数据
            LocalDateTime createdDateTime = createTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

            String weekOfMonthString = createdDateTime.format(DateTimeFormatter.ofPattern("W"));
            int weekOfMonth = Integer.parseInt(weekOfMonthString);
            int dayOfMonth = createdDateTime.getDayOfMonth();
            int monthValue = createdDateTime.getMonthValue();
            int yearValue = createdDateTime.getYear();
            // 获取当前时间
            LocalDateTime currentDateTime = LocalDateTime.now();

            // 计算时间差
            Duration duration = Duration.between(createdDateTime, currentDateTime);

            // 计算天数差
            long daysDiff = duration.toDays();

            // 未满 1 天算 1 天
            int day =  (int) (daysDiff == 0 ? 1 : daysDiff) + 1;
            day = Math.min(day, 7);
            list = initRecentWeekly(yearValue,monthValue,dayOfMonth,weekOfMonth,tempList, day);
        } else {

            tempList = linkMapper.select24StatsHourly(requestParam.getShort_url());
            list = initRecentDayly(tempList);
        }

        // 一天访问数据
        List<LinkStatsHourlyDTO> dayStats = linkMapper.selectLinkStatsHourly(requestParam.getShort_url());
        dayStats = initHourly(dayStats);
        // 一周访问数据
        List<LinkStatsWeeklyDTO> weekStats = linkMapper.selectLinkStatsWeekly(requestParam.getShort_url());
        weekStats = initWeekly(weekStats);
        // 操作系统访问数据
        List<LinkStatsOsDTO> osStats = linkMapper.selectLinkStatsOs(requestParam.getShort_url());
        // 浏览器访问数据
        List<LinkStatsBrowserDTO> browserStats = linkMapper.selectLinkStatsBrowser(requestParam.getShort_url());
        // 记录高频访问ip
        List<LinkStatsHighIpDTO> highIpStats = linkMapper.selectHighRatioIpLinkStats(requestParam.getShort_url());
        return LinkStatsRespDTO.builder()
                .pv(totalPv)
                // .uv(totalUv)
                .uip(totalUip)
                .list(list)
                .dayStats(dayStats)
                .weekStats(weekStats)
                .osStats(osStats)
                .browserStats(browserStats)
                .highIpStats(highIpStats)
                .build();
    }

    private List<LinkRecentStatsDTO> initRecentDayly(List<LinkRecentStatsDTO> list) {
        List<LinkRecentStatsDTO> list1 = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            list1.add(new LinkRecentStatsDTO(String.valueOf(i)));
        }
        for (LinkRecentStatsDTO linkStatsHourlyDTO : list) {
            list1.set(Integer.parseInt(linkStatsHourlyDTO.getId()), linkStatsHourlyDTO);
        }
        return list1;
    }

    private List<LinkRecentStatsDTO> initRecentWeekly(int year,int month, int day, int weekOfMonth,List<LinkRecentStatsDTO> list, int days) {
        String format = "%d-%d-%d";
        // List<LinkRecentStatsDTO> map = new ArrayList<>();
        HashMap<String, LinkRecentStatsDTO> map = new HashMap<>();

        for (int i = 0; i < days; i++) {
            String format1 = String.format(format, year, month, day + i);
            map.put(format1,new LinkRecentStatsDTO(format1));
        }
        for (LinkRecentStatsDTO recentStatsDTO : list) {
            String s = TimeUtil.formatDate(TimeUtil.getDateByWeekOfMonthAndDayOfWeek(weekOfMonth, Integer.parseInt(recentStatsDTO.getId()))).toString();
            map.put(s, LinkRecentStatsDTO.builder()
                            .id(s)
                            .pv(recentStatsDTO.getPv())
                            .uip(recentStatsDTO.getUip())
                            .build());
        }

        return new ArrayList<>(map.values());
    }

    private List<LinkStatsWeeklyDTO> initWeekly(List<LinkStatsWeeklyDTO> weekStats) {
        List<LinkStatsWeeklyDTO> linkStatsWeeklyDTOS = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            linkStatsWeeklyDTOS.add(new LinkStatsWeeklyDTO());
        }
        for (LinkStatsWeeklyDTO linkStatsWeeklyDTO :weekStats) {
            linkStatsWeeklyDTOS.set(linkStatsWeeklyDTO.getDay() - 1, linkStatsWeeklyDTO);
        }
        return linkStatsWeeklyDTOS;
    }
    //
    // @Override
    // public ShortLinkStatsRespDTO oneShortLinkStats(ShortLinkStatsReqDTO requestParam) {
    //     List<LinkAccessStatsDO> listStatsByShortLink = linkAccessStatsMapper.listStatsByShortLink(requestParam);
    //     if (CollUtil.isEmpty(listStatsByShortLink)) {
    //         return null;
    //     }
    //     // 基础访问数据
    //     LinkAccessStatsDO pvUvUidStatsByShortLink = linkAccessLogsMapper.findPvUvUidStatsByShortLink(requestParam);
    //     // 基础访问详情
    //     List<ShortLinkStatsAccessDailyRespDTO> daily = new ArrayList<>();
    //     List<String> rangeDates = DateUtil.rangeToList(DateUtil.parse(requestParam.getStartDate()), DateUtil.parse(requestParam.getEndDate()), DateField.DAY_OF_MONTH).stream()
    //             .map(DateUtil::formatDate)
    //             .toList();
    //     rangeDates.forEach(each -> listStatsByShortLink.stream()
    //             .filter(item -> Objects.equals(each, DateUtil.formatDate(item.getDate())))
    //             .findFirst()
    //             .ifPresentOrElse(item -> {
    //                 ShortLinkStatsAccessDailyRespDTO accessDailyRespDTO = ShortLinkStatsAccessDailyRespDTO.builder()
    //                         .date(each)
    //                         .pv(item.getPv())
    //                         .uv(item.getUv())
    //                         .uip(item.getUip())
    //                         .build();
    //                 daily.add(accessDailyRespDTO);
    //             }, () -> {
    //                 ShortLinkStatsAccessDailyRespDTO accessDailyRespDTO = ShortLinkStatsAccessDailyRespDTO.builder()
    //                         .date(each)
    //                         .pv(0)
    //                         .uv(0)
    //                         .uip(0)
    //                         .build();
    //                 daily.add(accessDailyRespDTO);
    //             }));
    //     // 地区访问详情（仅国内）
    //     List<ShortLinkStatsLocaleCNRespDTO> localeCnStats = new ArrayList<>();
    //     List<LinkLocaleStatsDO> listedLocaleByShortLink = linkLocaleStatsMapper.listLocaleByShortLink(requestParam);
    //     int localeCnSum = listedLocaleByShortLink.stream()
    //             .mapToInt(LinkLocaleStatsDO::getCnt)
    //             .sum();
    //     listedLocaleByShortLink.forEach(each -> {
    //         double ratio = (double) each.getCnt() / localeCnSum;
    //         double actualRatio = Math.round(ratio * 100.0) / 100.0;
    //         ShortLinkStatsLocaleCNRespDTO localeCNRespDTO = ShortLinkStatsLocaleCNRespDTO.builder()
    //                 .cnt(each.getCnt())
    //                 .locale(each.getProvince())
    //                 .ratio(actualRatio)
    //                 .build();
    //         localeCnStats.add(localeCNRespDTO);
    //     });
    //     // 小时访问详情
    //     List<Integer> hourStats = new ArrayList<>();
    //     List<LinkAccessStatsDO> listHourStatsByShortLink = linkAccessStatsMapper.listHourStatsByShortLink(requestParam);
    //     for (int i = 0; i < 24; i++) {
    //         AtomicInteger hour = new AtomicInteger(i);
    //         int hourCnt = listHourStatsByShortLink.stream()
    //                 .filter(each -> Objects.equals(each.getHour(), hour.get()))
    //                 .findFirst()
    //                 .map(LinkAccessStatsDO::getPv)
    //                 .orElse(0);
    //         hourStats.add(hourCnt);
    //     }
    //     // 高频访问IP详情
    //     List<ShortLinkStatsTopIpRespDTO> topIpStats = new ArrayList<>();
    //     List<HashMap<String, Object>> listTopIpByShortLink = linkAccessLogsMapper.listTopIpByShortLink(requestParam);
    //     listTopIpByShortLink.forEach(each -> {
    //         ShortLinkStatsTopIpRespDTO statsTopIpRespDTO = ShortLinkStatsTopIpRespDTO.builder()
    //                 .ip(each.get("ip").toString())
    //                 .cnt(Integer.parseInt(each.get("count").toString()))
    //                 .build();
    //         topIpStats.add(statsTopIpRespDTO);
    //     });
    //     // 一周访问详情
    //     List<Integer> weekdayStats = new ArrayList<>();
    //     List<LinkAccessStatsDO> listWeekdayStatsByShortLink = linkAccessStatsMapper.listWeekdayStatsByShortLink(requestParam);
    //     for (int i = 1; i < 8; i++) {
    //         AtomicInteger weekday = new AtomicInteger(i);
    //         int weekdayCnt = listWeekdayStatsByShortLink.stream()
    //                 .filter(each -> Objects.equals(each.getWeekday(), weekday.get()))
    //                 .findFirst()
    //                 .map(LinkAccessStatsDO::getPv)
    //                 .orElse(0);
    //         weekdayStats.add(weekdayCnt);
    //     }
    //     // 浏览器访问详情
    //     List<ShortLinkStatsBrowserRespDTO> browserStats = new ArrayList<>();
    //     List<HashMap<String, Object>> listBrowserStatsByShortLink = linkBrowserStatsMapper.listBrowserStatsByShortLink(requestParam);
    //     int browserSum = listBrowserStatsByShortLink.stream()
    //             .mapToInt(each -> Integer.parseInt(each.get("count").toString()))
    //             .sum();
    //     listBrowserStatsByShortLink.forEach(each -> {
    //         double ratio = (double) Integer.parseInt(each.get("count").toString()) / browserSum;
    //         double actualRatio = Math.round(ratio * 100.0) / 100.0;
    //         ShortLinkStatsBrowserRespDTO browserRespDTO = ShortLinkStatsBrowserRespDTO.builder()
    //                 .cnt(Integer.parseInt(each.get("count").toString()))
    //                 .browser(each.get("browser").toString())
    //                 .ratio(actualRatio)
    //                 .build();
    //         browserStats.add(browserRespDTO);
    //     });
    //     // 操作系统访问详情
    //     List<ShortLinkStatsOsRespDTO> osStats = new ArrayList<>();
    //     List<HashMap<String, Object>> listOsStatsByShortLink = linkOsStatsMapper.listOsStatsByShortLink(requestParam);
    //     int osSum = listOsStatsByShortLink.stream()
    //             .mapToInt(each -> Integer.parseInt(each.get("count").toString()))
    //             .sum();
    //     listOsStatsByShortLink.forEach(each -> {
    //         double ratio = (double) Integer.parseInt(each.get("count").toString()) / osSum;
    //         double actualRatio = Math.round(ratio * 100.0) / 100.0;
    //         ShortLinkStatsOsRespDTO osRespDTO = ShortLinkStatsOsRespDTO.builder()
    //                 .cnt(Integer.parseInt(each.get("count").toString()))
    //                 .os(each.get("os").toString())
    //                 .ratio(actualRatio)
    //                 .build();
    //         osStats.add(osRespDTO);
    //     });
    //     // 访客访问类型详情
    //     List<ShortLinkStatsUvRespDTO> uvTypeStats = new ArrayList<>();
    //     HashMap<String, Object> findUvTypeByShortLink = linkAccessLogsMapper.findUvTypeCntByShortLink(requestParam);
    //     int oldUserCnt = Integer.parseInt(
    //             Optional.ofNullable(findUvTypeByShortLink)
    //                     .map(each -> each.get("oldUserCnt"))
    //                     .map(Object::toString)
    //                     .orElse("0")
    //     );
    //     int newUserCnt = Integer.parseInt(
    //             Optional.ofNullable(findUvTypeByShortLink)
    //                     .map(each -> each.get("newUserCnt"))
    //                     .map(Object::toString)
    //                     .orElse("0")
    //     );
    //     int uvSum = oldUserCnt + newUserCnt;
    //     double oldRatio = (double) oldUserCnt / uvSum;
    //     double actualOldRatio = Math.round(oldRatio * 100.0) / 100.0;
    //     double newRatio = (double) newUserCnt / uvSum;
    //     double actualNewRatio = Math.round(newRatio * 100.0) / 100.0;
    //     ShortLinkStatsUvRespDTO newUvRespDTO = ShortLinkStatsUvRespDTO.builder()
    //             .uvType("newUser")
    //             .cnt(newUserCnt)
    //             .ratio(actualNewRatio)
    //             .build();
    //     uvTypeStats.add(newUvRespDTO);
    //     ShortLinkStatsUvRespDTO oldUvRespDTO = ShortLinkStatsUvRespDTO.builder()
    //             .uvType("oldUser")
    //             .cnt(oldUserCnt)
    //             .ratio(actualOldRatio)
    //             .build();
    //     uvTypeStats.add(oldUvRespDTO);
    //     // 访问设备类型详情
    //     List<ShortLinkStatsDeviceRespDTO> deviceStats = new ArrayList<>();
    //     List<LinkDeviceStatsDO> listDeviceStatsByShortLink = linkDeviceStatsMapper.listDeviceStatsByShortLink(requestParam);
    //     int deviceSum = listDeviceStatsByShortLink.stream()
    //             .mapToInt(LinkDeviceStatsDO::getCnt)
    //             .sum();
    //     listDeviceStatsByShortLink.forEach(each -> {
    //         double ratio = (double) each.getCnt() / deviceSum;
    //         double actualRatio = Math.round(ratio * 100.0) / 100.0;
    //         ShortLinkStatsDeviceRespDTO deviceRespDTO = ShortLinkStatsDeviceRespDTO.builder()
    //                 .cnt(each.getCnt())
    //                 .device(each.getDevice())
    //                 .ratio(actualRatio)
    //                 .build();
    //         deviceStats.add(deviceRespDTO);
    //     });
    //     // 访问网络类型详情
    //     List<ShortLinkStatsNetworkRespDTO> networkStats = new ArrayList<>();
    //     List<LinkNetworkStatsDO> listNetworkStatsByShortLink = linkNetworkStatsMapper.listNetworkStatsByShortLink(requestParam);
    //     int networkSum = listNetworkStatsByShortLink.stream()
    //             .mapToInt(LinkNetworkStatsDO::getCnt)
    //             .sum();
    //     listNetworkStatsByShortLink.forEach(each -> {
    //         double ratio = (double) each.getCnt() / networkSum;
    //         double actualRatio = Math.round(ratio * 100.0) / 100.0;
    //         ShortLinkStatsNetworkRespDTO networkRespDTO = ShortLinkStatsNetworkRespDTO.builder()
    //                 .cnt(each.getCnt())
    //                 .network(each.getNetwork())
    //                 .ratio(actualRatio)
    //                 .build();
    //         networkStats.add(networkRespDTO);
    //     });
    //     return ShortLinkStatsRespDTO.builder()
    //             .pv(pvUvUidStatsByShortLink.getPv())
    //             .uv(pvUvUidStatsByShortLink.getUv())
    //             .uip(pvUvUidStatsByShortLink.getUip())
    //             .daily(daily)
    //             .localeCnStats(localeCnStats)
    //             .hourStats(hourStats)
    //             .topIpStats(topIpStats)
    //             .weekdayStats(weekdayStats)
    //             .browserStats(browserStats)
    //             .osStats(osStats)
    //             .uvTypeStats(uvTypeStats)
    //             .deviceStats(deviceStats)
    //             .networkStats(networkStats)
    //             .build();
    // }


    /**
     * 初始化二十四小时数据
     */
    private List<LinkStatsHourlyDTO> initHourly(List<LinkStatsHourlyDTO> dayStats) {
        List<LinkStatsHourlyDTO> linkStatsHourlyDTOS = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            linkStatsHourlyDTOS.add(new LinkStatsHourlyDTO(i));
        }
        for (LinkStatsHourlyDTO linkStatsHourlyDTO : dayStats) {
            linkStatsHourlyDTOS.set(linkStatsHourlyDTO.getHour(), linkStatsHourlyDTO);
        }
        return linkStatsHourlyDTOS;
    }

    /**
     * 短链接数据统计
     */
    private void linkStats(String shortUri, HttpServletRequest request, HttpServletResponse response) {
        AtomicBoolean uvFirstFlag = new AtomicBoolean();
        Cookie[] cookies =  request.getCookies();
        AtomicReference<String> uv = new AtomicReference<>();
        Runnable addResponseCookieTask = () -> {
            uv.set(cn.hutool.core.lang.UUID.fastUUID().toString());
            Cookie uvCookie = new Cookie("uv", uv.get());
            uvCookie.setMaxAge(60 * 60 * 24 * 30);
            // uvCookie.setPath(StrUtil.sub(fullShortUrl, fullShortUrl.indexOf("/"), fullShortUrl.length()));
            response.addCookie(uvCookie);
            // 代表是第一次访问
            uvFirstFlag.set(Boolean.TRUE);
            // 存进redis
            stringRedisTemplate.opsForSet().add(SHORT_LINK_STATS_UV_KEY + shortUri, uv.get());
        };

        if (ArrayUtil.isNotEmpty(cookies)) {
            Arrays.stream(cookies)
                    .filter(each -> Objects.equals(each.getName(), "uv"))
                    .findFirst()
                    .map(Cookie::getValue)
                    .ifPresentOrElse(each -> {
                        uv.set(each);
                        // 如果能存进redis，则代表是第一次访问
                        Long uvAdded = stringRedisTemplate.opsForSet().add(SHORT_LINK_STATS_UV_KEY + shortUri, each);
                        uvFirstFlag.set(uvAdded != null && uvAdded > 0L);
                    }, addResponseCookieTask);
        } else {
            addResponseCookieTask.run();
        }

        String remoteAddr = request.getRemoteAddr();
        String temp = "114.247.50.2";
        String format = String.format(IP_GO_REGION_URL, temp, gaoDeKey);
        String localeJsonStr = HttpUtil.get(format);
        JSONObject localeJson = JSONUtil.parseObj(localeJsonStr);
        String province = localeJson.getStr("province");
        String city = localeJson.getStr("city");
        String os = LinkStatsUtil.getOs(request);
        String browser = LinkStatsUtil.getBrowser(request);
        // 当同一ip访问两个不同短链接时，两个短链接都应该被记录
        Long  ipAdd = stringRedisTemplate.opsForSet().add(String.format(LINK_IP_KEY, remoteAddr), shortUri);
        // if (ipAdd != null && ipAdd > 0) ipFlag.set(true);
        String gid = linkMapper.gotoByUri(shortUri);
        Date date = new Date();
        int day = DateUtil.dayOfWeek(date) - 1;
        int hour = DateUtil.hour(date, true);
        Long uipAdded = stringRedisTemplate.opsForSet().add(SHORT_LINK_STATS_UIP_KEY + shortUri, remoteAddr);
        boolean uipFirstFlag = uipAdded != null && uipAdded > 0L;
        LinkStats linkStats = LinkStats.builder()
                .gid(gid)
                .shortUri(shortUri)
                .ip(remoteAddr)
                .pv(1)
                .uv(uvFirstFlag.get()? 0 : 1)
                .uip(uipFirstFlag ? 0 : 1)
                .os(os)
                .province(province)
                .browser(browser)
                .city(city)
                .date(date)
                .week(day)
                .hour(hour)
                .build();
        //TODO 待修改
        linkStatsMapper.updateLinkStats(linkStats);
    }


    /**
     * 获取网站图标
     * @param urlString 网站地址
     * @return 网站图标地址
     */
    @SneakyThrows
    public String extractIconUrl(String urlString) {
        URL targetUrl = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        int responseCode = connection.getResponseCode();
        if (HttpURLConnection.HTTP_OK == responseCode) {
            Document document = Jsoup.connect(urlString).get();
            Element faviconLink = document.select("link[rel~=(?i)^(shortcut )?icon]").first();
            if (faviconLink != null) {
                return faviconLink.attr("abs:href");
            }
        }
        return null;
    }

    /**
     *
     * @param requestParam 短链接新增参数
     * @return 六位短链接
     * 获取短链接域名，获取原始链接拼接UUID，使用哈希函数转换为Base62的六位随机数，拼接域名和随机数，判断是否为空，如果已创建则再次生成短链接。未创建则返回六位随机数
     */
    public String generateSuffix(LinkCreateReqDTO requestParam) {
        String domain = URLParser.extractDomain(requestParam.getOriginUrl());
        String path = null;
        // 短链接需要保证唯一性(整个短链接)，所以需要判断其是否存在于数据库中，如果存在则不能创建
        // 这里海量数据判空使用布隆过滤器，布隆过滤器判断其存在时查询数据库(可能误判为存在)，如果
        // 数据库中也存在就代表存在，否则就代表不存在，可以创建
        int shortUrlCreateTimes = 0;
        while (true) {
            if (shortUrlCreateTimes > 10) {
                throw new ServiceException("短链接频繁创建，请稍后再试");
            }
            String originUrl = requestParam.getOriginUrl() + UUID.randomUUID();
            path = HashUtil.hashToBase62(originUrl);
            String fullShortUrl = domain + "/" + path;
            // 海量数据判空不推荐查数据库, 推荐使用布隆过滤器
            // 可能出现实际未创建，但误判其已创建的情况。这种情况下会再次生成短链接。
            if(!shortUrlCreateCachePenetrationBloomFilter.contains(fullShortUrl)){
                break;
            }
            shortUrlCreateTimes++;
        }
        return path;
    }
}
