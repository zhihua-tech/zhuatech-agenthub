/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agenthub;import cn.zhuatech.agenthub.service.CapacityRoutingService;import org.junit.jupiter.api.Test;import static org.junit.jupiter.api.Assertions.*;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
class CapacityRoutingServiceTests{private final CapacityRoutingService s=new CapacityRoutingService();/**
                                                                                                       * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                       */
@Test void addsCapacityForLongQueue(){var r=s.evaluate(new CapacityRoutingService.Request(100,2,2,5,30,10,true));assertEquals("ADD_CAPACITY",r.status());}/**
                                                                                                                                                                                                                                                                 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
                                                                                                                                                                                                                                                                 */
@Test void keepsShortQueueNormal(){var r=s.evaluate(new CapacityRoutingService.Request(2,5,5,5,30,0,true));assertEquals("NORMAL",r.status());}}
