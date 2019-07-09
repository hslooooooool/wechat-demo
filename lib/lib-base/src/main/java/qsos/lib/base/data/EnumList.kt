package qsos.lib.base.data

/**
 * @author : 华清松
 * @description : 中华名族字典
 */
enum class EnumNation(val key: String) {
    HA("汉族"),
    MG("蒙古族"),
    HU("回族"),
    ZA("藏族"),
    UG("维吾尔族"),
    MH("苗族"),
    YI("彝族"),
    ZH("壮族"),
    BY("布依族"),
    CS("朝鲜族"),
    MA("满族"),
    DO("侗族"),
    YA("瑶族"),
    BA("白族"),
    TJ("土家族"),
    HN("哈尼族"),
    KZ("哈萨克族"),
    DA("傣族"),
    LI("黎族"),
    LS("傈僳族"),
    VA("佤族"),
    SH("畲族"),
    GS("高山族"),
    LH("拉祜族"),
    SU("水族"),
    DX("东乡族"),
    NX("纳西族"),
    JP("景颇族"),
    KG("柯尔克孜族"),
    TU("土族"),
    DU("达斡尔族"),
    ML("仫佬族"),
    QI("羌族"),
    BL("布朗族"),
    SL("撒拉族"),
    MN("毛南族"),
    GL("仡佬族"),
    XB("锡伯族"),
    AC("阿昌族"),
    PM("普米族"),
    TA("塔吉克族"),
    NU("怒族"),
    UZ("乌兹别克族"),
    RS("俄罗斯族"),
    EW("鄂温克族"),
    DE("德昂族"),
    BN("保安族"),
    YG("裕固族"),
    GI("京族"),
    TT("塔塔尔族"),
    DR("独龙族"),
    OR("鄂伦春族"),
    HZ("赫哲族"),
    MB("门巴族"),
    LB("珞巴族"),
    JN("基诺族"),
}

/**
 * @author : 华清松
 * @description : 性别枚举
 */
enum class EnumSex(val key: String) {
    MAN("男"),
    WOMAN("女");
}

/**
 * @author : 华清松
 * @description : 布控级别枚举
 */
enum class EnumExecuteLevel(val key: String) {
    ONE("一级"),
    TWO("二级"),
    THREE("三级"),
    FOUR("四级");
}

/**
 * @author : 华清松
 * @description : 布控处置方式枚举
 */
enum class EnumExecuteDeal(val key: String) {
    DEAL1("关注"),
    DEAL2("干预"),
    DEAL3("盘查"),
    DEAL4("抓捕");
}

/**
 * @author : 华清松
 * @description : 标签类型枚举
 */
enum class EnumDict(val key: String, val type: String, val parentId: String) {
    SEX("性别", "sys_sex", "0"),
    NATION("民族", "bkxx_mz", "0"),
    USER_TYPE("人员类型", "person_type", "0"),
    EXECUTE_LEVEL("布控级别", "ps_grade", "0"),
    DEAL_TYPE("处置方式", "ps_handler", "0");
}