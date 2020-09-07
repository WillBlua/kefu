package com.chatopera.cc.controller;

//import com.alibaba.excel.EasyExcel;
//import com.baomidou.mybatisplus.core.metadata.IPage;
import com.alibaba.fastjson.JSONObject;
import com.chatopera.cc.acd.ACDPolicyService;
import com.chatopera.cc.acd.ACDWorkMonitor;
import com.chatopera.cc.basic.Constants;
import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.basic.MainUtils;
import com.chatopera.cc.cache.Cache;
import com.chatopera.cc.model.*;
import com.chatopera.cc.persistence.blob.JpaBlobHelper;
import com.chatopera.cc.persistence.es.ContactsRepository;
import com.chatopera.cc.persistence.repository.*;
import com.chatopera.cc.proxy.OnlineUserProxy;
import com.chatopera.cc.util.*;
import com.chatopera.cc.vo.Result;
//import com.framework.core.utils.BeanCopyUtils;
//import com.framework.core.utils.EasyExcelUtils;
//import com.framework.core.utils.FileUplodUtils;
//import com.framework.core.vo.Result;
//import com.framework.web.demo.entity.DemoEntity;
//import com.framework.web.demo.listener.DemoExcelListener;
//import com.framework.web.demo.srevice.DemoService;
//import com.framework.web.demo.vo.DemoVo;
import io.minio.MinioClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * <p> ClassName: com.framework.web.demo.controller.DemoController.java </p>
 * <p> Description : DemoController.java demo 开发示例</p>
 * <p> Author : jfwu </p>
 * <p> Version : 1.0 </p>
 * <p> Create Time : 2020/4/29 13:40 </p>
 * <p> Author Email: <a href="mailTo:2391923921@qq.com">jfwu</a> </p>
 */
@Slf4j
@RestController
@Api(tags = "DEMO示例")
@RequestMapping("/apiController")
public class DemoController extends Handler {

//    @Autowired
//    private DemoService demoService;

    @Autowired
    private MinioClient minioClient;

    @Value("${miniourl}")
    private String miniourl;

    @Value("${uk.im.server.port}")
    private Integer port;

    @Value("${cs.im.server.ssl.port}")
    private Integer sslPort;

    @Value("${cskefu.settings.webim.visitor-separate}")
    private Boolean channelWebIMVisitorSeparate;

    @Autowired
    private Cache cache;

    @Autowired
    private ACDWorkMonitor acdWorkMonitor;

    @Autowired
    private ACDPolicyService acdPolicyService;

    @Autowired
    private OnlineUserRepository onlineUserRes;

    @Autowired
    private StreamingFileRepository streamingFileRepository;

    @Autowired
    private JpaBlobHelper jpaBlobHelper;

    @Autowired
    private ConsultInviteRepository inviteRepository;

    @Autowired
    private ChatMessageRepository chatMessageRes;

    @Autowired
    private AgentServiceSatisRepository agentServiceSatisRes;

    @Autowired
    private AgentServiceRepository agentServiceRepository;

    @Autowired
    private InviteRecordRepository inviteRecordRes;

    @Autowired
    private LeaveMsgRepository leaveMsgRes;

    @Autowired
    private AgentUserRepository agentUserRepository;

    @Autowired
    private AttachmentRepository attachementRes;

    @Autowired
    private ContactsRepository contactsRes;

    @Autowired
    private AgentUserContactsRepository agentUserContactsRes;

    @Autowired
    private SNSAccountRepository snsAccountRepository;

    @Autowired
    private SNSAccountRepository snsAccountRes;

    @Autowired
    private UserHistoryRepository userHistoryRes;

    @Autowired
    private ChatbotRepository chatbotRes;

//    @Value("${axwayapi}")
//    private String apiUrl;

//    @ApiOperation("测试示例")
//    @GetMapping("/getdata/{json}")
//    public Result getData(@ApiParam("示例ID") @PathVariable(value = "json") String json){
//        String data = "";
//        data = HttpReq.post( apiUrl + "/CEB311Message/Search",json);
//        return Result.ok(data);
//    }

//    /**
//     * 新增示例
//     *
//     * @param demoVo
//     * @return
//     */
//    @ApiOperation("新增示例")
//    @PostMapping("/insert")
//    public Result insert(@Valid @RequestBody DemoVo demoVo) {
//        demoService.save(demoVo);
//        return Result.ok();
//    }

//    /**
//     * 删除示例
//     *
//     * @param id
//     * @return
//     */
//    @ApiOperation("删除示例")
//    @DeleteMapping("/delete/{id}")
//    public Result delete(@ApiParam("示例ID") @PathVariable(value = "id") String id) {
//        demoService.removeById(id);
//        return Result.ok();
//    }

//    /**
//     * 获取示例
//     *
//     * @param id
//     * @return
//     */
//    @ApiOperation("获取对象")
//    @GetMapping("/getById/{id}")
//    public Result getById(@ApiParam("示例ID") @PathVariable(value = "id") String id) {
//        DemoEntity entity = demoService.getById(id);
//        return Result.ok(entity);
//    }

//    /**
//     * 修改示例
//     *
//     * @param demoVo
//     * @return
//     */
//    @ApiOperation("修改示例")
//    @PutMapping("/update")
//    public Result update(@Valid @RequestBody DemoVo demoVo) {
//        demoService.save(demoVo);
//        return Result.ok();
//    }

//    /**
//     * 分页查询
//     *
//     * @param pageNum
//     * @param pageSize
//     * @param demoVo
//     * @return
//     */
//    @ApiOperation("分页查询")
//    @RequestMapping(value = "/list", method = RequestMethod.POST)
//    public Result getTestList(@RequestParam(name = "pageNum", required = false, defaultValue = "1") int pageNum,
//                              @RequestParam(name = "pageSize", required = false, defaultValue = "15") int pageSize,
//                              @RequestBody DemoVo demoVo) {
//        IPage<DemoEntity> pageList = demoService.searchList(demoVo, pageNum, pageSize);
//        log.debug("查询当前页：" + pageList.getCurrent());
//        log.debug("查询当前页数量：" + pageList.getSize());
//        log.debug("查询结果数量：" + pageList.getRecords().size());
//        log.debug("数据总数：" + pageList.getTotal());
//        return Result.ok(pageList);
//    }

//    /**
//     * 模板导出
//     *
//     * @param response
//     */
//    @ApiOperation("下载模板")
//    @GetMapping("/down")
//    public void down(HttpServletResponse response) {
//        EasyExcelUtils.writeStyleExcel(response, new ArrayList<>(), "导入模板", "sheet", DemoVo.class);
//    }

//    /**
//     * Excel Web导出示例
//     * Api导出JSON即可
//     *
//     * @param response
//     */
//    @ApiOperation("导出示例")
//    @GetMapping("/export")
//    public void exp(HttpServletResponse response) {
//        String fileName = "测试数据导出";
//        String sheetName = "sheet1";
//        try {
//            List<DemoVo> list = new ArrayList<>();
//            List<DemoEntity> entityList = demoService.list();
//            BeanCopyUtils.copy(entityList, list, DemoVo.class);
//            EasyExcelUtils.writeStyleExcel(response, list, fileName, sheetName, DemoVo.class);
//        } catch (Exception e) {
//            log.error("数据导出异常:", e);
//        }
//    }

//    /**
//     * Excel导入示例
//     *
//     * @param file
//     * @return
//     * @throws IOException
//     */
//    @ResponseBody
//    @ApiOperation("导入示例")
//    @PostMapping("/import")
//    public Result imp(MultipartFile file) throws IOException {
//        EasyExcel.read(file.getInputStream(), DemoVo.class, new DemoExcelListener(demoService)).sheet().doRead();
//        return Result.ok("导入成功！");
//    }

    /**
     * Excel Web导出示例
     * Api导出JSON即可
     *
     * @param file
     */
    @ApiOperation("文件上传")
    @PostMapping("/upload")
    public Result upload(MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }
        if (file == null || file.getSize() == 0) {
            return Result.error("上传文件不能为空");
        }
        try {
            Map map = FileUplodUtils.upload(file);
            return Result.ok(map);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Result.error("上传失败！");
    }

    /**
     * 文件下载
     * http://127.0.0.1:8081/demo/download?fileName=89e16d04-04d3-42bf-aa00-8b726c46838c.png
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    @ApiOperation("文件下载")
    @GetMapping("/download")
    public void download(HttpServletResponse response, @RequestParam(value = "fileName", required = true) String fileName) {
        boolean status = fileName.contains(miniourl);
        if(status){
            fileName=fileName.replace(miniourl,"");
        }
        FileUplodUtils.download(response,fileName);
    }

    /**
     * 图片预览
     * http://127.0.0.1:8081/demo/view?fileName=89e16d04-04d3-42bf-aa00-8b726c46838c.png
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    @ApiOperation("图片预览")
    @GetMapping("view")
    public void view(HttpServletResponse response, @RequestParam(value = "fileName", required = true) String fileName) {
        boolean status = fileName.contains(miniourl);
        if (status){
            fileName = fileName.replace(miniourl,"");
        }

        FileUplodUtils.view(response,fileName);
    }

    //访问地址
    @ApiOperation("/获取Socket")
    @RequestMapping("/getUrlInfo/{appid}")
//    @RequestMapping("/text/{appid}")
    @Menu(type = "im", subtype = "index", access = true)
    public String text(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String appid,
            @Valid String traceid,
            @Valid String aiid,
            @Valid String exchange,
            @Valid String title,
            @Valid String url,
            @Valid String skill,
            @Valid String id,
            @Valid String userid,
            @Valid String agent,
            @Valid String name,
            @Valid String email,
            @Valid String phone,
            @Valid String ai,
            @Valid String orgi,
            @Valid String product,
            @Valid String description,
            @Valid String imgurl,
            @Valid String pid,
            @Valid String purl) throws Exception {
//        ModelAndView view = request(super.createRequestPageTempletResponse("/apps/im/text"));
            ModelMap map=new ModelMap();

            JSONObject jsonObject = new JSONObject();
            CousultInvite invite = OnlineUserProxy.consult(
                    appid, StringUtils.isBlank(orgi) ? MainContext.SYSTEM_ORGI : orgi);

            jsonObject.put("hostname", request.getServerName());
            jsonObject.put("port", request.getServerPort());
            jsonObject.put("schema", request.getScheme());
            jsonObject.put("appid", appid);
            jsonObject.put("channelVisitorSeparate", channelWebIMVisitorSeparate);
            jsonObject.put("ip", MainUtils.md5(request.getRemoteAddr()));

            if (invite.isSkill() && invite.isConsult_skill_fixed()) { // 添加技能组ID
                // 忽略前端传入的技能组ID
                jsonObject.put("skill", invite.getConsult_skill_fixed_id());
            } else if (StringUtils.isNotBlank(skill)) {
                jsonObject.put("skill", skill);
            }

            if (StringUtils.isNotBlank(agent)) {
                jsonObject.put("agent", agent);
            }

            jsonObject.put("client", MainUtils.getUUID());
            jsonObject.put("sessionid", request.getSession().getId());

            jsonObject.put("id", id);
            if (StringUtils.isNotBlank(ai)) {
                jsonObject.put("ai", ai);
            }
            if (StringUtils.isNotBlank(exchange)) {
                jsonObject.put("exchange", exchange);
            }

            jsonObject.put("name", name);
            jsonObject.put("email", email);
            jsonObject.put("phone", phone);
            jsonObject.put("userid", userid);

            jsonObject.put("product", product);
            jsonObject.put("description", description);
            jsonObject.put("imgurl", imgurl);
            jsonObject.put("pid", pid);
            jsonObject.put("purl", purl);

            if (StringUtils.isNotBlank(traceid)) {
                jsonObject.put("traceid", traceid);
            }
            if (StringUtils.isNotBlank(title)) {
                jsonObject.put("title", title);
            }
            if (StringUtils.isNotBlank(traceid)) {
                jsonObject.put("url", url);
            }

            if (invite != null) {
                jsonObject.put("inviteData", invite);
                jsonObject.put("orgi", invite.getOrgi());
                jsonObject.put("appid", appid);

                if (StringUtils.isNotBlank(aiid)) {
                    jsonObject.put("aiid", aiid);
                } else if (StringUtils.isNotBlank(invite.getAiid())) {
                    jsonObject.put("aiid", invite.getAiid());
                }
            }
            Contacts contacts =new Contacts();
            //构建随机用户码
            String uuid = UUID.randomUUID().toString().replace("-","");
            JSONObject indexJson=index(map,request,response,orgi,aiid,traceid,exchange,title,url,"",phone,ai,"","",appid,uuid,"",skill,agent,contacts,product,description,imgurl,pid,purl,false);
//            JSONObject.toJSON(indexJson);
            jsonObject.put("index",indexJson);
            return jsonObject.toJSONString();
//            return Result.ok(jsonObject.toJSONString());


    }





    //访问地址
//    @ApiOperation("/text")
//    @RequestMapping("/text/{appid}")
//    @Menu(type = "im", subtype = "index", access = true)
//    public ModelAndView text(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            @PathVariable String appid,
//            @Valid String traceid,
//            @Valid String aiid,
//            @Valid String exchange,
//            @Valid String title,
//            @Valid String url,
//            @Valid String skill,
//            @Valid String id,
//            @Valid String userid,
//            @Valid String agent,
//            @Valid String name,
//            @Valid String email,
//            @Valid String phone,
//            @Valid String ai,
//            @Valid String orgi,
//            @Valid String product,
//            @Valid String description,
//            @Valid String imgurl,
//            @Valid String pid,
//            @Valid String purl) throws Exception {
//        ModelAndView view = request(super.createRequestPageTempletResponse("/apps/im/text"));
//        CousultInvite invite = OnlineUserProxy.consult(
//                appid, StringUtils.isBlank(orgi) ? MainContext.SYSTEM_ORGI : orgi);
//
//        view.addObject("hostname", request.getServerName());
//        view.addObject("port", request.getServerPort());
//        view.addObject("schema", request.getScheme());
//        view.addObject("appid", appid);
//        view.addObject("channelVisitorSeparate", channelWebIMVisitorSeparate);
//        view.addObject("ip", MainUtils.md5(request.getRemoteAddr()));
//
//        if (invite.isSkill() && invite.isConsult_skill_fixed()) { // 添加技能组ID
//            // 忽略前端传入的技能组ID
//            view.addObject("skill", invite.getConsult_skill_fixed_id());
//        } else if (StringUtils.isNotBlank(skill)) {
//            view.addObject("skill", skill);
//        }
//
//        if (StringUtils.isNotBlank(agent)) {
//            view.addObject("agent", agent);
//        }
//
//        view.addObject("client", MainUtils.getUUID());
//        view.addObject("sessionid", request.getSession().getId());
//
//        view.addObject("id", id);
//        if (StringUtils.isNotBlank(ai)) {
//            view.addObject("ai", ai);
//        }
//        if (StringUtils.isNotBlank(exchange)) {
//            view.addObject("exchange", exchange);
//        }
//
//        view.addObject("name", name);
//        view.addObject("email", email);
//        view.addObject("phone", phone);
//        view.addObject("userid", userid);
//
//        view.addObject("product", product);
//        view.addObject("description", description);
//        view.addObject("imgurl", imgurl);
//        view.addObject("pid", pid);
//        view.addObject("purl", purl);
//
//        if (StringUtils.isNotBlank(traceid)) {
//            view.addObject("traceid", traceid);
//        }
//        if (StringUtils.isNotBlank(title)) {
//            view.addObject("title", title);
//        }
//        if (StringUtils.isNotBlank(traceid)) {
//            view.addObject("url", url);
//        }
//
//        if (invite != null) {
//            view.addObject("inviteData", invite);
//            view.addObject("orgi", invite.getOrgi());
//            view.addObject("appid", appid);
//
//            if (StringUtils.isNotBlank(aiid)) {
//                view.addObject("aiid", aiid);
//            } else if (StringUtils.isNotBlank(invite.getAiid())) {
//                view.addObject("aiid", invite.getAiid());
//            }
//        }
//
//        return view;
//    }



//    public JSONObject index(
//            ModelMap map,
//            HttpServletRequest request,
//            HttpServletResponse response,
//            @Valid final String orgi,
//            @Valid final String aiid,
////                              @Valid String uid,
//            @Valid final String traceid,
//            @Valid final String exchange,
//            @Valid final String title,
//            @Valid final String url,
//            @Valid final String mobile,
//            @Valid final String phone,
//            @Valid final String ai,
//            @Valid final String client,
//            @Valid final String type,
//            @Valid final String appid,
//            @Valid final String userid,
//            @Valid final String sessionid,
//            @Valid final String skill,
//            @Valid final String agent,
//            @Valid Contacts contacts,
//            @Valid final String product,
//            @Valid final String description,
//            @Valid final String imgurl,
//            @Valid final String pid,
//            @Valid final String purl,
//            @Valid final boolean isInvite) throws Exception {
//        JSONObject jsonObject=new JSONObject();
//        log.info(
//                "[index] orgi {}, skill {}, agent {}, traceid {}, isInvite {}, exchange {}", orgi, skill, agent,
//                traceid, isInvite, exchange);
//        Map<String, String> sessionMessageObj = cache.findOneSystemMapByIdAndOrgi(sessionid, orgi);
//
//        if (sessionMessageObj != null) {
//            request.getSession().setAttribute("Sessionusername", sessionMessageObj.get("username"));
//            request.getSession().setAttribute("Sessioncid", sessionMessageObj.get("cid"));
//            request.getSession().setAttribute("Sessioncompany_name", sessionMessageObj.get("company_name"));
//            request.getSession().setAttribute("Sessionsid", sessionMessageObj.get("sid"));
//            request.getSession().setAttribute("Sessionsystem_name", sessionMessageObj.get("system_name"));
//            request.getSession().setAttribute("sessionid", sessionMessageObj.get("sessionid"));
//            request.getSession().setAttribute("Sessionuid", sessionMessageObj.get("uid"));
//        }
//        //控制指向前台路径路径
////        ModelAndView view = request(super.createRequestPageTempletResponse("/apps/im/index2"));
//        Optional<BlackEntity> blackOpt = cache.findOneBlackEntityByUserIdAndOrgi(userid, MainContext.SYSTEM_ORGI);
//        if (StringUtils.isNotBlank(
//                appid) && ((!blackOpt.isPresent()) || (blackOpt.get().getEndtime() != null && blackOpt.get().getEndtime().before(
//                new Date())))) {
//            CousultInvite invite = OnlineUserProxy.consult(appid, orgi);
//            String randomUserId; // 随机生成OnlineUser的用户名，使用了浏览器指纹做唯一性KEY
//            if (StringUtils.isNotBlank(userid)) {
//                randomUserId = MainUtils.genIDByKey(userid);
//            } else {
//                randomUserId = MainUtils.genIDByKey(sessionid);
//            }
//            String nickname;
//
//            if (sessionMessageObj != null) {
//                nickname = ((Map) sessionMessageObj).get("username") + "@" + ((Map) sessionMessageObj).get(
//                        "company_name");
//            } else if (request.getSession().getAttribute("Sessionusername") != null) {
//                String struname = (String) request.getSession().getAttribute("Sessionusername");
//                String strcname = (String) request.getSession().getAttribute("Sessioncompany_name");
//                nickname = struname + "@" + strcname;
//            } else {
//                nickname = "Guest_" + "@" + randomUserId;
//            }
//
//            jsonObject.put("nickname", nickname);
//
//            boolean consult = true;                //是否已收集用户信息
//            SessionConfig sessionConfig = acdPolicyService.initSessionConfig(orgi);
//
//            // 强制开启满意调查问卷
//            sessionConfig.setSatisfaction(true);
//
//            map.addAttribute("sessionConfig", sessionConfig);
//            map.addAttribute("hostname", request.getServerName());
//
//            if (sslPort != null) {
//                map.addAttribute("port", sslPort);
//            } else {
//                map.addAttribute("port", port);
//            }
//
//            map.addAttribute("appid", appid);
//            map.addAttribute("userid", userid);
//            map.addAttribute("schema", request.getScheme());
//            map.addAttribute("sessionid", sessionid);
//            map.addAttribute("isInvite", isInvite);
//
//
//            jsonObject.put("product", product);
//            jsonObject.put("description", description);
//            jsonObject.put("imgurl", imgurl);
//            jsonObject.put("pid", pid);
//            jsonObject.put("purl", purl);
//
//            map.addAttribute("ip", MainUtils.md5(request.getRemoteAddr()));
//
//            if (StringUtils.isNotBlank(traceid)) {
//                map.addAttribute("traceid", traceid);
//            }
//            if (StringUtils.isNotBlank(exchange)) {
//                map.addAttribute("exchange", exchange);
//            }
//            if (StringUtils.isNotBlank(title)) {
//                map.addAttribute("title", title);
//            }
//            if (StringUtils.isNotBlank(traceid)) {
//                map.addAttribute("url", url);
//            }
//
//            map.addAttribute("cskefuport", request.getServerPort());
//
//            /**
//             * 先检查 invite不为空
//             */
//            if (invite != null) {
//                log.info("[index] invite id {}, orgi {}", invite.getId(), invite.getOrgi());
//                map.addAttribute("orgi", invite.getOrgi());
//                map.addAttribute("inviteData", invite);
//
//                if (StringUtils.isNotBlank(aiid)) {
//                    map.addAttribute("aiid", aiid);
//                } else if (StringUtils.isNotBlank(invite.getAiid())) {
//                    map.addAttribute("aiid", invite.getAiid());
//                }
//
//                AgentReport report;
//                if (invite.isSkill() && invite.isConsult_skill_fixed()) { // 绑定技能组
//                    report = acdWorkMonitor.getAgentReport(invite.getConsult_skill_fixed_id(), invite.getOrgi());
//                } else {
//                    report = acdWorkMonitor.getAgentReport(invite.getOrgi());
//                }
//
//                boolean isLeavemsg = false;
//                if (report.getAgents() == 0 ||
//                        (sessionConfig.isHourcheck() &&
//                                !MainUtils.isInWorkingHours(sessionConfig.getWorkinghours()) &&
//                                invite.isLeavemessage())) {
//                    // 没有坐席在线，进入留言
//                    isLeavemsg = true;
//                    boolean isInWorkingHours = MainUtils.isInWorkingHours(sessionConfig.getWorkinghours());
//                    map.addAttribute("isInWorkingHours", isInWorkingHours);
////                    view = request(super.createRequestPageTempletResponse("/apps/im/leavemsg"));
//                } else if (invite.isConsult_info()) {    //启用了信息收集，从Request获取， 或从 Cookies 里去
//                    // 验证 OnlineUser 信息
//                    if (contacts != null && StringUtils.isNotBlank(
//                            contacts.getName())) {    //contacts用于传递信息，并不和 联系人表发生 关联，contacts信息传递给 Socket.IO，然后赋值给 AgentUser，最终赋值给 AgentService永久存储
//                        consult = true;
//                        //存入 Cookies
//                        if (invite.isConsult_info_cookies()) {
//                            Cookie name = new Cookie(
//                                    "name", MainUtils.encryption(URLEncoder.encode(contacts.getName(), "UTF-8")));
//                            response.addCookie(name);
//                            name.setMaxAge(3600);
//                            if (StringUtils.isNotBlank(contacts.getPhone())) {
//                                Cookie phonecookie = new Cookie(
//                                        "phone", MainUtils.encryption(URLEncoder.encode(contacts.getPhone(), "UTF-8")));
//                                phonecookie.setMaxAge(3600);
//                                response.addCookie(phonecookie);
//                            }
//                            if (StringUtils.isNotBlank(contacts.getEmail())) {
//                                Cookie email = new Cookie(
//                                        "email", MainUtils.encryption(URLEncoder.encode(contacts.getEmail(), "UTF-8")));
//                                email.setMaxAge(3600);
//                                response.addCookie(email);
//                            }
//
//                            if (StringUtils.isNotBlank(contacts.getSkypeid())) {
//                                Cookie skypeid = new Cookie(
//                                        "skypeid", MainUtils.encryption(
//                                        URLEncoder.encode(contacts.getSkypeid(), "UTF-8")));
//                                skypeid.setMaxAge(3600);
//                                response.addCookie(skypeid);
//                            }
//
//
//                            if (StringUtils.isNotBlank(contacts.getMemo())) {
//                                Cookie memo = new Cookie(
//                                        "memo", MainUtils.encryption(URLEncoder.encode(contacts.getName(), "UTF-8")));
//                                memo.setMaxAge(3600);
//                                response.addCookie(memo);
//                            }
//                        }
//                    } else {
//                        //从 Cookies里尝试读取
//                        if (invite.isConsult_info_cookies()) {
//                            Cookie[] cookies = request.getCookies();//这样便可以获取一个cookie数组
//                            contacts = new Contacts();
//                            if (cookies != null) {
//                                for (Cookie cookie : cookies) {
//                                    if (cookie != null && StringUtils.isNotBlank(
//                                            cookie.getName()) && StringUtils.isNotBlank(cookie.getValue())) {
//                                        if (cookie.getName().equals("name")) {
//                                            contacts.setName(URLDecoder.decode(
//                                                    MainUtils.decryption(cookie.getValue()),
//                                                    "UTF-8"));
//                                        }
//                                        if (cookie.getName().equals("phone")) {
//                                            contacts.setPhone(URLDecoder.decode(
//                                                    MainUtils.decryption(cookie.getValue()),
//                                                    "UTF-8"));
//                                        }
//                                        if (cookie.getName().equals("email")) {
//                                            contacts.setEmail(URLDecoder.decode(
//                                                    MainUtils.decryption(cookie.getValue()),
//                                                    "UTF-8"));
//                                        }
//                                        if (cookie.getName().equals("memo")) {
//                                            contacts.setMemo(URLDecoder.decode(
//                                                    MainUtils.decryption(cookie.getValue()),
//                                                    "UTF-8"));
//                                        }
//                                        if (cookie.getName().equals("skypeid")) {
//                                            contacts.setSkypeid(
//                                                    URLDecoder.decode(
//                                                            MainUtils.decryption(cookie.getValue()),
//                                                            "UTF-8"));
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                        if (StringUtils.isBlank(contacts.getName())) {
//                            consult = false;
////                            view = request(super.createRequestPageTempletResponse("/apps/im/collecting"));
//                        }
//                    }
//                } else {
//                    // TODO 该contacts的识别并不准确，因为不能关联
////                    contacts = OnlineUserProxy.processContacts(invite.getOrgi(), contacts, appid, userid);
//                    String uid = (String) request.getSession().getAttribute("Sessionuid");
//                    String sid = (String) request.getSession().getAttribute("Sessionsid");
//                    String cid = (String) request.getSession().getAttribute("Sessioncid");
//
//                    if (StringUtils.isNotBlank(uid) && StringUtils.isNotBlank(sid) && StringUtils.isNotBlank(cid)) {
//                        Contacts contacts1 = contactsRes.findOneByWluidAndWlsidAndWlcidAndDatastatus(
//                                uid, sid, cid, false);
//                        if (contacts1 != null) {
//                            agentUserRepository.findOneByUseridAndOrgi(userid, orgi).ifPresent(p -> {
//                                // 关联AgentService的联系人
//                                if (StringUtils.isNotBlank(p.getAgentserviceid())) {
//                                    AgentService agentService = agentServiceRepository.findOne(
//                                            p.getAgentserviceid());
//                                    agentService.setContactsid(contacts1.getId());
//                                }
//
//                                // 关联AgentUserContact的联系人
//                                // NOTE: 如果该userid已经有了关联的Contact则忽略，继续使用之前的
//                                Optional<AgentUserContacts> agentUserContactsOpt = agentUserContactsRes.findOneByUseridAndOrgi(
//                                        userid, orgi);
//                                if (!agentUserContactsOpt.isPresent()) {
//                                    AgentUserContacts agentUserContacts = new AgentUserContacts();
//                                    agentUserContacts.setOrgi(orgi);
//                                    agentUserContacts.setAppid(appid);
//                                    agentUserContacts.setChannel(p.getChannel());
//                                    agentUserContacts.setContactsid(contacts1.getId());
//                                    agentUserContacts.setUserid(userid);
//                                    agentUserContacts.setUsername(
//                                            (String) request.getSession().getAttribute("Sessionusername"));
////                                    agentUserContacts.setCreater(super.getUser(request).getId());
//                                    agentUserContacts.setCreatetime(new Date());
//                                    agentUserContactsRes.save(agentUserContacts);
//                                }
//                            });
//                        }
//                    }
//                }
//
//                if (StringUtils.isNotBlank(client)) {
//                    map.addAttribute("client", client);
//                }
//
//                if (StringUtils.isNotBlank(skill)) {
//                    map.addAttribute("skill", skill);
//                }
//
//                if (StringUtils.isNotBlank(agent)) {
//                    map.addAttribute("agent", agent);
//                }
//
//                map.addAttribute("contacts", contacts);
//
//                if (StringUtils.isNotBlank(type)) {
//                    map.addAttribute("type", type);
//                }
//                IP ipdata = IPTools.getInstance().findGeography(MainUtils.getIpAddr(request));
//                map.addAttribute("skillGroups", OnlineUserProxy.organ(invite.getOrgi(), ipdata, invite, true));
//
//                if (invite != null && consult) {
//                    if (contacts != null && StringUtils.isNotBlank(contacts.getName())) {
//                        nickname = contacts.getName();
//                    }
//
//                    map.addAttribute("username", nickname);
//                    boolean isChatbotAgentFirst = false;
//                    boolean isEnableExchangeAgentType = false;
//                    Chatbot bot = null;
//
//                    // 是否使用机器人客服
//                    if (invite.isAi() && MainContext.hasModule(Constants.CSKEFU_MODULE_CHATBOT)) {
//                        // 查找机器人
//                        bot = chatbotRes.findOne(invite.getAiid());
//                        if (bot != null) {
//                            // 判断是否接受访客切换坐席类型
//                            isEnableExchangeAgentType = !StringUtils.equals(
//                                    bot.getWorkmode(), Constants.CHATBOT_CHATBOT_ONLY);
//
//                            // 判断是否机器人客服优先
//                            if (((StringUtils.equals(
//                                    ai, "true")) || (invite.isAifirst() && ai == null))) {
//                                isChatbotAgentFirst = true;
//                            }
//                        }
//                    }
//
//                    map.addAttribute(
//                            "exchange", isEnableExchangeAgentType);
//
//                    if (isChatbotAgentFirst) {
//                        // 机器人坐席
//                        HashMap<String, String> chatbotConfig = new HashMap<String, String>();
//                        chatbotConfig.put("botname", invite.getAiname());
//                        chatbotConfig.put("botid", invite.getAiid());
//                        chatbotConfig.put("botwelcome", invite.getAimsg());
//                        chatbotConfig.put("botfirst", Boolean.toString(invite.isAifirst()));
//                        chatbotConfig.put("isai", Boolean.toString(invite.isAi()));
//
//
//                        if (chatbotConfig != null) {
//                            map.addAttribute("chatbotConfig", chatbotConfig);
//                        }
////                        view = request(super.createRequestPageTempletResponse("/apps/im/chatbot/index"));
//                        if (MobileDevice.isMobile(request.getHeader("User-Agent")) || StringUtils.isNotBlank(
//                                mobile)) {
////                            view = request(super.createRequestPageTempletResponse(
////                                    "/apps/im/chatbot/mobile"));        // 智能机器人 移动端
//                        }
//                    } else {
//                        // 维持人工坐席的设定，检查是否进入留言
//                        if (!isLeavemsg && (MobileDevice.isMobile(
//                                request.getHeader("User-Agent")) || StringUtils.isNotBlank(mobile))) {
////                            view = request(
////                                    super.createRequestPageTempletResponse("/apps/im/mobile"));    // WebIM移动端。再次点选技能组？
//                        }
//                    }
//
//                    map.addAttribute(
//                            "chatMessageList", chatMessageRes.findByUsessionAndOrgi(userid, orgi, new PageRequest(0, 20,
//                                    Sort.Direction.DESC,
//                                    "updatetime")));
//                }
//                jsonObject.put("commentList", Dict.getInstance().getDic(Constants.CSKEFU_SYSTEM_COMMENT_DIC));
//                jsonObject.put("commentItemList", Dict.getInstance().getDic(Constants.CSKEFU_SYSTEM_COMMENT_ITEM_DIC));
//                jsonObject.put("welcomeAd", MainUtils.getPointAdv(MainContext.AdPosEnum.WELCOME.toString(), orgi));
//                jsonObject.put("imageAd", MainUtils.getPointAdv(MainContext.AdPosEnum.IMAGE.toString(), orgi));
//
//                // 确定"接受邀请"被处理后，通知浏览器关闭弹出窗口
//                OnlineUserProxy.sendWebIMClients(userid, "accept");
//
//                // 更新InviteRecord
//                log.info("[index] update inviteRecord for user {}", userid);
//                final Date threshold = new Date(System.currentTimeMillis() - Constants.WEBIM_AGENT_INVITE_TIMEOUT);
//                Page<InviteRecord> inviteRecords = inviteRecordRes.findByUseridAndOrgiAndResultAndCreatetimeGreaterThan(
//                        userid, orgi,
//                        MainContext.OnlineUserInviteStatus.DEFAULT.toString(),
//                        threshold, new PageRequest(0, 1, Sort.Direction.DESC, "createtime"));
//                if (inviteRecords.getContent() != null && inviteRecords.getContent().size() > 0) {
//                    final InviteRecord record = inviteRecords.getContent().get(0);
//                    record.setUpdatetime(new Date());
//                    record.setTraceid(traceid);
//                    record.setTitle(title);
//                    record.setUrl(url);
//                    record.setResponsetime((int) (System.currentTimeMillis() - record.getCreatetime().getTime()));
//                    record.setResult(MainContext.OnlineUserInviteStatus.ACCEPT.toString());
//                    log.info("[index] re-save inviteRecord id {}", record.getId());
//                    inviteRecordRes.save(record);
//                }
//
//            } else {
//                log.info("[index] can not invite for appid {}, orgi {}", appid, orgi);
//            }
//        }
//
//        log.info("[index] return view");
//        return jsonObject;
//    }




    /****/

    @RequestMapping("/index")
    @Menu(type = "im", subtype = "index", access = true)
    public JSONObject index(
            ModelMap map,
            HttpServletRequest request,
            HttpServletResponse response,
            @Valid  String orgi,
            @Valid  String aiid,
//                              @Valid String uid,
            @Valid  String traceid,
            @Valid  String exchange,
            @Valid  String title,
            @Valid  String url,
            @Valid  String mobile,
            @Valid  String phone,
            @Valid  String ai,
            @Valid  String client,
            @Valid  String type,
            @Valid  String appid,
            @Valid  String userid,
            @Valid  String sessionid,
            @Valid  String skill,
            @Valid  String agent,
            @Valid Contacts contacts,
            @Valid  String product,
            @Valid  String description,
            @Valid  String imgurl,
            @Valid  String pid,
            @Valid  String purl,
            @Valid  boolean isInvite) throws Exception {
        log.info(
                "[index] orgi {}, skill {}, agent {}, traceid {}, isInvite {}, exchange {}", orgi, skill, agent,
                traceid, isInvite, exchange);
        Map<String, String> sessionMessageObj = cache.findOneSystemMapByIdAndOrgi(sessionid, orgi);
        JSONObject jsonObject=new JSONObject();

        if (sessionMessageObj != null) {
            request.getSession().setAttribute("Sessionusername", sessionMessageObj.get("username"));
            jsonObject.put("username",sessionMessageObj.get("username"));
            request.getSession().setAttribute("Sessioncid", sessionMessageObj.get("cid"));
            jsonObject.put("cid",sessionMessageObj.get("cid"));
            request.getSession().setAttribute("Sessioncompany_name", sessionMessageObj.get("company_name"));
            jsonObject.put( "company_name",sessionMessageObj.get("company_name"));
            request.getSession().setAttribute("Sessionsid", sessionMessageObj.get("sid"));
            jsonObject.put("sid" ,sessionMessageObj.get("sid"));
            request.getSession().setAttribute("Sessionsystem_name", sessionMessageObj.get("system_name"));
            jsonObject.put("system_name",sessionMessageObj.get("system_name"));
            request.getSession().setAttribute("sessionid", sessionMessageObj.get("sessionid"));
            jsonObject.put("sessionid",sessionMessageObj.get("sessionid"));
            request.getSession().setAttribute("Sessionuid", sessionMessageObj.get("uid"));
            jsonObject.put("uid",sessionMessageObj.get("uid"));
        }
        //控制指向前台路径路径
        ModelAndView view = request(super.createRequestPageTempletResponse("/apps/im/index2"));
        Optional<BlackEntity> blackOpt = cache.findOneBlackEntityByUserIdAndOrgi(userid, MainContext.SYSTEM_ORGI);
        if (StringUtils.isNotBlank(
                appid) && ((!blackOpt.isPresent()) || (blackOpt.get().getEndtime() != null && blackOpt.get().getEndtime().before(
                new Date())))) {
            CousultInvite invite = OnlineUserProxy.consult(appid, orgi);
            String randomUserId; // 随机生成OnlineUser的用户名，使用了浏览器指纹做唯一性KEY
            if (StringUtils.isNotBlank(userid)) {
                randomUserId = MainUtils.genIDByKey(userid);
                jsonObject.put("genIDByKey",userid);
            } else {
                randomUserId = MainUtils.genIDByKey(sessionid);
                jsonObject.put("genIDByKey",sessionid);
            }
            String nickname;

            if (sessionMessageObj != null) {
                nickname = ((Map) sessionMessageObj).get("username") + "@" + ((Map) sessionMessageObj).get(
                        "company_name");
                jsonObject.put("nickname",nickname);
            } else if (request.getSession().getAttribute("Sessionusername") != null) {
                String struname = (String) request.getSession().getAttribute("Sessionusername");
                String strcname = (String) request.getSession().getAttribute("Sessioncompany_name");
                nickname = struname + "@" + strcname;
                jsonObject.put("nickname",nickname);
            } else {
                nickname = "Guest_" + "@" + randomUserId;
            }

            jsonObject.put("nickname", nickname);

            boolean consult = true;                //是否已收集用户信息
            SessionConfig sessionConfig = acdPolicyService.initSessionConfig(orgi);

            // 强制开启满意调查问卷
            sessionConfig.setSatisfaction(true);

            map.addAttribute("sessionConfig", sessionConfig);
            jsonObject.put("sessionConfig",sessionConfig);
            map.addAttribute("hostname", request.getServerName());
            jsonObject.put("hostname",request.getServerName());

            if (sslPort != null) {
                map.addAttribute("port", sslPort);
                jsonObject.put("port",sslPort);
            } else {
                map.addAttribute("port", port);
                jsonObject.put("port",port);
            }

            map.addAttribute("appid", appid);
            jsonObject.put("appid",appid);
            map.addAttribute("userid", userid);
            jsonObject.put("userid",userid);
            map.addAttribute("schema", request.getScheme());
            jsonObject.put("schema",request.getScheme());
            map.addAttribute("sessionid", sessionid);
            map.addAttribute("isInvite", isInvite);
            jsonObject.put("isInvite",isInvite);


            view.addObject("product", product);
            view.addObject("description", description);
            view.addObject("imgurl", imgurl);
            view.addObject("pid", pid);
            view.addObject("purl", purl);
            jsonObject.put("product", product);
            jsonObject.put("description", description);
            jsonObject.put("imgurl", imgurl);
            jsonObject.put("pid", pid);
            jsonObject.put("purl", purl);

            map.addAttribute("ip", MainUtils.md5(request.getRemoteAddr()));

            if (StringUtils.isNotBlank(traceid)) {
                map.addAttribute("traceid", traceid);
                jsonObject.put("traceid", traceid);
            }
            if (StringUtils.isNotBlank(exchange)) {
                map.addAttribute("exchange", exchange);
                jsonObject.put("exchange", exchange);
            }
            if (StringUtils.isNotBlank(title)) {
                map.addAttribute("title", title);
                jsonObject.put("title", title);
            }
            if (StringUtils.isNotBlank(traceid)) {
                map.addAttribute("url", url);
                jsonObject.put("url", url);
            }

            map.addAttribute("cskefuport", request.getServerPort());
            jsonObject.put("cskefuport", request.getServerPort());

            /**
             * 先检查 invite不为空
             */
            if (invite != null) {
                log.info("[index] invite id {}, orgi {}", invite.getId(), invite.getOrgi());
                map.addAttribute("orgi", invite.getOrgi());
                map.addAttribute("inviteData", invite);
                jsonObject.put("orgi", invite.getOrgi());
                jsonObject.put("inviteData", invite);

                if (StringUtils.isNotBlank(aiid)) {
                    map.addAttribute("aiid", aiid);
                    jsonObject.put("aiid", aiid);
                } else if (StringUtils.isNotBlank(invite.getAiid())) {
                    map.addAttribute("aiid", invite.getAiid());
                    jsonObject.put("aiid", invite.getAiid());
                }

                AgentReport report;
                if (invite.isSkill() && invite.isConsult_skill_fixed()) { // 绑定技能组
                    report = acdWorkMonitor.getAgentReport(invite.getConsult_skill_fixed_id(), invite.getOrgi());
                } else {
                    report = acdWorkMonitor.getAgentReport(invite.getOrgi());
                }

                boolean isLeavemsg = false;
                if (report.getAgents() == 0 ||
                        (sessionConfig.isHourcheck() &&
                                !MainUtils.isInWorkingHours(sessionConfig.getWorkinghours()) &&
                                invite.isLeavemessage())) {
                    // 没有坐席在线，进入留言
                    isLeavemsg = true;
                    boolean isInWorkingHours = MainUtils.isInWorkingHours(sessionConfig.getWorkinghours());
                    map.addAttribute("isInWorkingHours", isInWorkingHours);
                    jsonObject.put("isInWorkingHours", isInWorkingHours);
                    view = request(super.createRequestPageTempletResponse("/apps/im/leavemsg"));
                } else if (invite.isConsult_info()) {    //启用了信息收集，从Request获取， 或从 Cookies 里去
                    // 验证 OnlineUser 信息
                    if (contacts != null && StringUtils.isNotBlank(
                            contacts.getName())) {    //contacts用于传递信息，并不和 联系人表发生 关联，contacts信息传递给 Socket.IO，然后赋值给 AgentUser，最终赋值给 AgentService永久存储
                        consult = true;
                        //存入 Cookies
                        if (invite.isConsult_info_cookies()) {
                            Cookie name = new Cookie(
                                    "name", MainUtils.encryption(URLEncoder.encode(contacts.getName(), "UTF-8")));
                            response.addCookie(name);
                            name.setMaxAge(3600);
                            if (StringUtils.isNotBlank(contacts.getPhone())) {
                                Cookie phonecookie = new Cookie(
                                        "phone", MainUtils.encryption(URLEncoder.encode(contacts.getPhone(), "UTF-8")));
                                phonecookie.setMaxAge(3600);
                                response.addCookie(phonecookie);
                            }
                            if (StringUtils.isNotBlank(contacts.getEmail())) {
                                Cookie email = new Cookie(
                                        "email", MainUtils.encryption(URLEncoder.encode(contacts.getEmail(), "UTF-8")));
                                email.setMaxAge(3600);
                                response.addCookie(email);
                            }

                            if (StringUtils.isNotBlank(contacts.getSkypeid())) {
                                Cookie skypeid = new Cookie(
                                        "skypeid", MainUtils.encryption(
                                        URLEncoder.encode(contacts.getSkypeid(), "UTF-8")));
                                skypeid.setMaxAge(3600);
                                response.addCookie(skypeid);
                            }


                            if (StringUtils.isNotBlank(contacts.getMemo())) {
                                Cookie memo = new Cookie(
                                        "memo", MainUtils.encryption(URLEncoder.encode(contacts.getName(), "UTF-8")));
                                memo.setMaxAge(3600);
                                response.addCookie(memo);
                            }
                        }
                    } else {
                        //从 Cookies里尝试读取
                        if (invite.isConsult_info_cookies()) {
                            Cookie[] cookies = request.getCookies();//这样便可以获取一个cookie数组
                            contacts = new Contacts();
                            if (cookies != null) {
                                for (Cookie cookie : cookies) {
                                    if (cookie != null && StringUtils.isNotBlank(
                                            cookie.getName()) && StringUtils.isNotBlank(cookie.getValue())) {
                                        if (cookie.getName().equals("name")) {
                                            contacts.setName(URLDecoder.decode(
                                                    MainUtils.decryption(cookie.getValue()),
                                                    "UTF-8"));
                                        }
                                        if (cookie.getName().equals("phone")) {
                                            contacts.setPhone(URLDecoder.decode(
                                                    MainUtils.decryption(cookie.getValue()),
                                                    "UTF-8"));
                                        }
                                        if (cookie.getName().equals("email")) {
                                            contacts.setEmail(URLDecoder.decode(
                                                    MainUtils.decryption(cookie.getValue()),
                                                    "UTF-8"));
                                        }
                                        if (cookie.getName().equals("memo")) {
                                            contacts.setMemo(URLDecoder.decode(
                                                    MainUtils.decryption(cookie.getValue()),
                                                    "UTF-8"));
                                        }
                                        if (cookie.getName().equals("skypeid")) {
                                            contacts.setSkypeid(
                                                    URLDecoder.decode(
                                                            MainUtils.decryption(cookie.getValue()),
                                                            "UTF-8"));
                                        }
                                    }
                                }
                            }
                        }
                        if (StringUtils.isBlank(contacts.getName())) {
                            consult = false;
                            view = request(super.createRequestPageTempletResponse("/apps/im/collecting"));
                        }
                    }
                    jsonObject.put("map",map);
                } else {
                    // TODO 该contacts的识别并不准确，因为不能关联
//                    contacts = OnlineUserProxy.processContacts(invite.getOrgi(), contacts, appid, userid);
                    String uid = (String) request.getSession().getAttribute("Sessionuid");
                    String sid = (String) request.getSession().getAttribute("Sessionsid");
                    String cid = (String) request.getSession().getAttribute("Sessioncid");

                    if (StringUtils.isNotBlank(uid) && StringUtils.isNotBlank(sid) && StringUtils.isNotBlank(cid)) {
                        Contacts contacts1 = contactsRes.findOneByWluidAndWlsidAndWlcidAndDatastatus(
                                uid, sid, cid, false);
                        if (contacts1 != null) {
                            agentUserRepository.findOneByUseridAndOrgi(userid, orgi).ifPresent(p -> {
                                // 关联AgentService的联系人
                                if (StringUtils.isNotBlank(p.getAgentserviceid())) {
                                    AgentService agentService = agentServiceRepository.findOne(
                                            p.getAgentserviceid());
                                    agentService.setContactsid(contacts1.getId());
                                }

                                // 关联AgentUserContact的联系人
                                // NOTE: 如果该userid已经有了关联的Contact则忽略，继续使用之前的
                                Optional<AgentUserContacts> agentUserContactsOpt = agentUserContactsRes.findOneByUseridAndOrgi(
                                        userid, orgi);
                                if (!agentUserContactsOpt.isPresent()) {
                                    AgentUserContacts agentUserContacts = new AgentUserContacts();
                                    agentUserContacts.setOrgi(orgi);
                                    agentUserContacts.setAppid(appid);
                                    agentUserContacts.setChannel(p.getChannel());
                                    agentUserContacts.setContactsid(contacts1.getId());
                                    agentUserContacts.setUserid(userid);
                                    agentUserContacts.setUsername(
                                            (String) request.getSession().getAttribute("Sessionusername"));
                                    agentUserContacts.setCreater(super.getUser(request).getId());
                                    agentUserContacts.setCreatetime(new Date());
                                    agentUserContactsRes.save(agentUserContacts);
                                }
                            });
                        }
                    }
                }

                if (StringUtils.isNotBlank(client)) {
                    map.addAttribute("client", client);
                    jsonObject.put("client", client);
                }

                if (StringUtils.isNotBlank(skill)) {
                    map.addAttribute("skill", skill);
                    jsonObject.put("skill", skill);
                }

                if (StringUtils.isNotBlank(agent)) {
                    map.addAttribute("agent", agent);
                    jsonObject.put("agent", agent);
                }

                map.addAttribute("contacts", contacts);
                jsonObject.put("contacts", contacts);

                if (StringUtils.isNotBlank(type)) {
                    map.addAttribute("type", type);
                    jsonObject.put("type", type);
                }
                IP ipdata = IPTools.getInstance().findGeography(MainUtils.getIpAddr(request));
                map.addAttribute("skillGroups", OnlineUserProxy.organ(invite.getOrgi(), ipdata, invite, true));
                jsonObject.put("skillGroups", OnlineUserProxy.organ(invite.getOrgi(), ipdata, invite, true));

                if (invite != null && consult) {
                    if (contacts != null && StringUtils.isNotBlank(contacts.getName())) {
                        nickname = contacts.getName();
                    }

                    map.addAttribute("username", nickname);
                    jsonObject.put("username", nickname);
                    boolean isChatbotAgentFirst = false;
                    boolean isEnableExchangeAgentType = false;
                    Chatbot bot = null;

                    // 是否使用机器人客服
                    if (invite.isAi() && MainContext.hasModule(Constants.CSKEFU_MODULE_CHATBOT)) {
                        // 查找机器人
                        bot = chatbotRes.findOne(invite.getAiid());
                        if (bot != null) {
                            // 判断是否接受访客切换坐席类型
                            isEnableExchangeAgentType = !StringUtils.equals(
                                    bot.getWorkmode(), Constants.CHATBOT_CHATBOT_ONLY);

                            // 判断是否机器人客服优先
                            if (((StringUtils.equals(
                                    ai, "true")) || (invite.isAifirst() && ai == null))) {
                                isChatbotAgentFirst = true;
                            }
                        }
                    }

                    map.addAttribute(
                            "exchange", isEnableExchangeAgentType);
                    jsonObject.put("exchange", isEnableExchangeAgentType);

                    if (isChatbotAgentFirst) {
                        // 机器人坐席
                        HashMap<String, String> chatbotConfig = new HashMap<String, String>();
                        chatbotConfig.put("botname", invite.getAiname());
                        chatbotConfig.put("botid", invite.getAiid());
                        chatbotConfig.put("botwelcome", invite.getAimsg());
                        chatbotConfig.put("botfirst", Boolean.toString(invite.isAifirst()));
                        chatbotConfig.put("isai", Boolean.toString(invite.isAi()));


                        if (chatbotConfig != null) {
                            map.addAttribute("chatbotConfig", chatbotConfig);
                        }
                        view = request(super.createRequestPageTempletResponse("/apps/im/chatbot/index"));
                        if (MobileDevice.isMobile(request.getHeader("User-Agent")) || StringUtils.isNotBlank(
                                mobile)) {
                            view = request(super.createRequestPageTempletResponse(
                                    "/apps/im/chatbot/mobile"));        // 智能机器人 移动端
                        }
                    } else {
                        // 维持人工坐席的设定，检查是否进入留言
                        if (!isLeavemsg && (MobileDevice.isMobile(
                                request.getHeader("User-Agent")) || StringUtils.isNotBlank(mobile))) {
                            view = request(
                                    super.createRequestPageTempletResponse("/apps/im/mobile"));    // WebIM移动端。再次点选技能组？
                        }
                    }

                    map.addAttribute(
                            "chatMessageList", chatMessageRes.findByUsessionAndOrgi(userid, orgi, new PageRequest(0, 20,
                                    Sort.Direction.DESC,
                                    "updatetime")));
                }
//                jsonObject.put("commentList", Dict.getInstance().getDic(Constants.CSKEFU_SYSTEM_COMMENT_DIC));
//                jsonObject.put("commentItemList", Dict.getInstance().getDic(Constants.CSKEFU_SYSTEM_COMMENT_ITEM_DIC));
                jsonObject.put("welcomeAd", MainUtils.getPointAdv(MainContext.AdPosEnum.WELCOME.toString(), orgi));
                jsonObject.put("imageAd", MainUtils.getPointAdv(MainContext.AdPosEnum.IMAGE.toString(), orgi));

                // 确定"接受邀请"被处理后，通知浏览器关闭弹出窗口
                OnlineUserProxy.sendWebIMClients(userid, "accept");

                // 更新InviteRecord
                log.info("[index] update inviteRecord for user {}", userid);
                final Date threshold = new Date(System.currentTimeMillis() - Constants.WEBIM_AGENT_INVITE_TIMEOUT);
                Page<InviteRecord> inviteRecords = inviteRecordRes.findByUseridAndOrgiAndResultAndCreatetimeGreaterThan(
                        userid, orgi,
                        MainContext.OnlineUserInviteStatus.DEFAULT.toString(),
                        threshold, new PageRequest(0, 1, Sort.Direction.DESC, "createtime"));
                if (inviteRecords.getContent() != null && inviteRecords.getContent().size() > 0) {
                    final InviteRecord record = inviteRecords.getContent().get(0);
                    record.setUpdatetime(new Date());
                    record.setTraceid(traceid);
                    record.setTitle(title);
                    record.setUrl(url);
                    record.setResponsetime((int) (System.currentTimeMillis() - record.getCreatetime().getTime()));
                    record.setResult(MainContext.OnlineUserInviteStatus.ACCEPT.toString());
                    log.info("[index] re-save inviteRecord id {}", record.getId());
                    inviteRecordRes.save(record);
                }

            } else {
                log.info("[index] can not invite for appid {}, orgi {}", appid, orgi);
            }
        }

        log.info("[index] return view");
        return jsonObject;
//        return view;
    }

}
