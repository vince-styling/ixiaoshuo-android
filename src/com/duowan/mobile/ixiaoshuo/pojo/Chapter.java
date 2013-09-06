package com.duowan.mobile.ixiaoshuo.pojo;

import com.duowan.mobile.ixiaoshuo.utils.PaginationList;

public class Chapter {
	private int id;				// 章节ID，来自服务器
	private String title;

	private int readStatus;
	public static final int READSTATUS_UNREAD       = 0; // 未读
	public static final int READSTATUS_READING      = 1; // 在读
	public static final int READSTATUS_READ         = 2; // 已读
	private int beginPosition;

	public Chapter() {}

	public Chapter(int id, String title) {
		this.id = id;
		this.title = title;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setChapterId(int chapterId) {
		this.id = chapterId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getReadStatus() {
		return readStatus;
	}

	public boolean isReading() {
		return readStatus == READSTATUS_READING;
	}

	public boolean isUnRead() {
		return readStatus == READSTATUS_UNREAD;
	}

	public boolean isRead() {
		return readStatus == READSTATUS_READ;
	}

	public void setReadStatus(int readStatus) {
		this.readStatus = readStatus;
	}

	public int getBeginPosition() {
		return beginPosition;
	}

	public void setBeginPosition(int beginPosition) {
		this.beginPosition = beginPosition;
	}

	public static PaginationList<Chapter> getChapterList(int pageNo, int pageItemCount) {
		PaginationList<Chapter> list = new PaginationList<Chapter>();

		list.add(new Chapter(10050625, "第001章 送来的女人"));
		list.add(new Chapter(10050631, "第002章 天上掉下个小恶魔"));
		list.add(new Chapter(10051578, "第003章 大叔你温柔点"));
		list.add(new Chapter(10053436, "第004章 老男人脏死了"));
		list.add(new Chapter(10053442, "第005章 爱像天使守护你"));
		list.add(new Chapter(10053450, "第006章 是不是搞错对象了"));
		list.add(new Chapter(10053456, "第007章 没穿衣服的姐姐"));
		list.add(new Chapter(10053512, "第008章 你来替她"));
		list.add(new Chapter(10067288, "第009章 我替她受罚"));
		list.add(new Chapter(10072533, "第010章 乖乖听话"));
		list.add(new Chapter(10072540, "第011章 这个家很温馨"));
		list.add(new Chapter(10072609, "第012章 他只是有点怪"));
		list.add(new Chapter(10072688, "第013章 神秘女人"));
		list.add(new Chapter(10072781, "第014章 慕辰受伤"));
		list.add(new Chapter(10072846, "第015章 大叔我恐高"));
		list.add(new Chapter(10073113, "第016章 舍命相护"));
		list.add(new Chapter(10080669, "第017章 相拥而眠"));
		list.add(new Chapter(10080674, "第018章 我是他的女人"));
		list.add(new Chapter(10080681, "第019章 有了你的宝宝"));
		list.add(new Chapter(10080685, "第020章 不负责任的男人"));
		list.add(new Chapter(10099756, "第021章 我们谈谈"));
		list.add(new Chapter(10099791, "第022章 宝宝是这样来的"));
		list.add(new Chapter(10099820, "第023章 大叔，晚安"));
		list.add(new Chapter(10149229, "第024章 公司绯色事件"));
		list.add(new Chapter(10149354, "第025章 我是萌主我怕谁"));
		list.add(new Chapter(10149483, "第026章 大叔你乖点"));
		list.add(new Chapter(10149611, "第027章 我要自由"));
		list.add(new Chapter(10181836, "第028章 什么时候长大"));
		list.add(new Chapter(10181897, "第029章 谁是阿谨"));
		list.add(new Chapter(10182001, "第030章 记住你是我的女人"));
		list.add(new Chapter(10182114, "第031章 只要你乖乖的"));
		list.add(new Chapter(10182197, "第032章 基友团来了"));
		list.add(new Chapter(10241943, "第033章 极品基友"));
		list.add(new Chapter(10248706, "第034章 你敢喝她的东西"));
		list.add(new Chapter(10248709, "第035章 真心话游戏"));
		list.add(new Chapter(10248710, "第036章 他生气了"));
		list.add(new Chapter(10248712, "第037章 我不想再等"));
		list.add(new Chapter(10280944, "第038章 你好神哦"));
		list.add(new Chapter(10285589, "第039章 女人乖乖听话"));
		list.add(new Chapter(10303447, "第040章 究竟是个怎样的女人"));
		list.add(new Chapter(10313670, "第041章 没地方可去"));
		list.add(new Chapter(10328022, "第042章 再入狼窝"));
		list.add(new Chapter(10337136, "第043章 她不能出事"));
		list.add(new Chapter(10347148, "第044章 让她晕过去吧"));
		list.add(new Chapter(10356093, "第045章 什么叫引狼入室"));
		list.add(new Chapter(10377158, "第046章 浴室索情"));
		list.add(new Chapter(10387068, "第047章 他是魔鬼"));
		list.add(new Chapter(10387443, "第048章 不会不要你"));
		list.add(new Chapter(10404412, "第049章 江湖规矩"));
		list.add(new Chapter(10414328, "第050章 新的任务"));
		list.add(new Chapter(10415904, "第051章 没有不接的道理"));
		list.add(new Chapter(10416482, "第052章 凌语夕你变了"));
		list.add(new Chapter(10445780, "第053章 我要和你分手"));
		list.add(new Chapter(10452776, "第054章 没有资格说分手"));
		list.add(new Chapter(10468087, "第055章 心乱"));
		list.add(new Chapter(10476692, "第056章 这里，这里好疼"));
		list.add(new Chapter(10488096, "第057章 我会乖乖听话"));
		list.add(new Chapter(10492951, "第058章 少打我女人的主意"));
		list.add(new Chapter(10495905, "第059章 我不要"));
		list.add(new Chapter(10502949, "第060章 学着接受我"));
		list.add(new Chapter(10506826, "第061章 两个男人的较量"));
		list.add(new Chapter(10508663, "第062章 屏保上的女孩"));
		list.add(new Chapter(10509477, "第063章 除非他是牙签"));
		list.add(new Chapter(10540991, "第064章 你看你的，我亲我的"));
		list.add(new Chapter(10541070, "第065章 这次来真的"));
		list.add(new Chapter(10541115, "第066章 还是那么喜欢你"));
		list.add(new Chapter(10554397, "第067章 敢偷袭我的男人"));
		list.add(new Chapter(10554689, "第068章 他死不了"));
		list.add(new Chapter(10555712, "第069章 必须离开他"));
		list.add(new Chapter(10565668, "第070章 等你过完生日"));
		list.add(new Chapter(10609673, "第071章 再不相见"));
		list.add(new Chapter(10609413, "第072章 转身就忘了"));
		list.add(new Chapter(10610834, "第073章 哪里长大了"));
		list.add(new Chapter(10615126, "第074章 丢失的炎皇令"));
		list.add(new Chapter(10615174, "第075章 新的角色"));
		list.add(new Chapter(10615237, "第076章 今夜会有飓风"));
		list.add(new Chapter(10615324, "第077章 能不能往别的地方看"));
		list.add(new Chapter(10664627, "第078章 丢脸么"));
		list.add(new Chapter(10692102, "第079章 命都是她的"));
		list.add(new Chapter(10697969, "第080章 你太虚弱"));
		list.add(new Chapter(10706647, "第081章 我和你不一样"));
		list.add(new Chapter(10721091, "第082章 飓风来袭"));
		list.add(new Chapter(10721787, "第083章 早把你看光了"));
		list.add(new Chapter(10733184, "第084章 为什么选择我"));
		list.add(new Chapter(10752764, "第085章 撩人的闺中密语"));
		list.add(new Chapter(10757633, "第086章 他好不要脸"));
		list.add(new Chapter(10761507, "第087章 这个上午很有激情"));
		list.add(new Chapter(10762538, "第088章 船长室疑案"));
		list.add(new Chapter(10770864, "第089章 他现在在做什么"));
		list.add(new Chapter(10799801, "第090章 穆贵妃陵"));
		list.add(new Chapter(10799806, "第091章 神秘的荒漠"));
		list.add(new Chapter(10799812, "第092章 将来要嫁给莫名"));
		list.add(new Chapter(10799971, "第093章 一年一度元祖典"));
		list.add(new Chapter(10799974, "第094章 折翼的天使"));
		list.add(new Chapter(10799975, "第095章 看清楚我是什么人"));
		list.add(new Chapter(10799979, "第096章 你又不乖了"));
		list.add(new Chapter(10799982, "第097章 是你自找的"));
		list.add(new Chapter(10812977, "第098章 不要告诉阿谨"));
		list.add(new Chapter(10813028, "第099章 如果你想这样"));
		list.add(new Chapter(10813085, "第100章 报酬我还是会要的"));
		list.add(new Chapter(10822940, "第101章 我会一直等你"));
		list.add(new Chapter(10823014, "第102章 我要那种药"));
		list.add(new Chapter(10831444, "第103章 和动物有什么区别"));
		list.add(new Chapter(10831454, "第104章 参不透的图形"));
		list.add(new Chapter(10831466, "第105章 小心肾虚"));
		list.add(new Chapter(10838036, "第106章 给我生个宝宝"));
		list.add(new Chapter(10838052, "第107章 被劫"));
		list.add(new Chapter(10838108, "第108章 你再说一次"));
		list.add(new Chapter(10838143, "第109章 你不吃我就吃你"));
		list.add(new Chapter(10838152, "第110章 害怕了吗"));
		list.add(new Chapter(10849526, "第111章 恶劣的男人"));
		list.add(new Chapter(10850117, "第112章 莫名生气了"));
		list.add(new Chapter(10851136, "第113章 要不我们结婚吧"));
		list.add(new Chapter(10851413, "第114章 婚前体验"));
		list.add(new Chapter(10851547, "第115章 月黑风高"));
		list.add(new Chapter(10859136, "第116章 我也要和你睡"));
		list.add(new Chapter(10860549, "第117章 他只有一个"));
		list.add(new Chapter(10861243, "第118章 这男人好帅"));
		list.add(new Chapter(10861722, "第119章 这仇，我记住了"));
		list.add(new Chapter(10868495, "第120章 我也要嫁给你"));
		list.add(new Chapter(10869606, "第121章 越来越喜欢你了"));
		list.add(new Chapter(10871112, "第122章 被撞见"));
		list.add(new Chapter(10878646, "第123章 多余的人"));
		list.add(new Chapter(10878655, "第124章 第一次"));
		list.add(new Chapter(10880560, "第125章 真是个二货"));
		list.add(new Chapter(10885111, "第126章 什么玩意儿"));
		list.add(new Chapter(10885153, "第127章 她是我的女朋友"));
		list.add(new Chapter(10890043, "第128章 如此报复"));
		list.add(new Chapter(10890114, "第129章 带你去好玩的地方"));
		list.add(new Chapter(10890495, "第130章 崇拜我么"));
		list.add(new Chapter(10897574, "第131章 怎么舍得丢下你"));
		list.add(new Chapter(10897629, "第132章 那个女孩"));
		list.add(new Chapter(10901321, "第133章 一直在和你谈恋爱"));
		list.add(new Chapter(10905998, "第134章 惊魂之夜"));
		list.add(new Chapter(10906161, "第135章 这笔帐怎么算"));
		list.add(new Chapter(10911648, "第136章 想他吗"));
		list.add(new Chapter(10921273, "第137章 好不好嘛"));
		list.add(new Chapter(10921589, "第138章 我什么都答应"));
		list.add(new Chapter(10921781, "第139章 怎么做到的"));
		list.add(new Chapter(10930028, "第140章 炎族后代"));
		list.add(new Chapter(10931041, "第141章 让她回台川"));
		list.add(new Chapter(10933461, "第142章 七星连珠"));
		list.add(new Chapter(10940603, "第143章 不如这样"));
		list.add(new Chapter(10941338, "第144章 我是她的男人"));
		list.add(new Chapter(10944044, "第145章 炎皇令的秘密"));
		list.add(new Chapter(10950006, "第146章 你做梦"));
		list.add(new Chapter(10951213, "第147章 只要和我睡一夜"));
		list.add(new Chapter(10951696, "第148章 怪异的古井"));
		list.add(new Chapter(10951757, "第149章 天浩"));
		list.add(new Chapter(10954865, "第150章 到我房间让你碰个够"));
		list.add(new Chapter(10960830, "第151章 我和他一样"));
		list.add(new Chapter(10962144, "第152章 她也是你的女人吗"));
		list.add(new Chapter(10962384, "第153章 如果可以选择"));
		list.add(new Chapter(10966027, "第154章 今夜有行动"));
		list.add(new Chapter(10966030, "第155章 这算什么逻辑"));
		list.add(new Chapter(10971747, "第156章 这招挺管用"));
		list.add(new Chapter(10980313, "第157章 被发现了"));
		list.add(new Chapter(10980986, "第158章 你来做什么"));
		list.add(new Chapter(10982183, "第159章 抓回去玩玩"));
		list.add(new Chapter(10984291, "第160章 今晚陪你喝酒"));
		list.add(new Chapter(10990158, "第161章 玩完就灭口吧"));
		list.add(new Chapter(10992031, "第162章 不会太血腥"));
		list.add(new Chapter(10996703, "第163章 你们这些禽兽"));
		list.add(new Chapter(10996731, "第164章 能不能小心点"));
		list.add(new Chapter(11002476, "第165章 真的很禽兽"));
		list.add(new Chapter(11008224, "第166章 十二点到了"));
		list.add(new Chapter(11009506, "第167章 你会怎样"));
		list.add(new Chapter(11019329, "第168章 别再伤害她"));
		list.add(new Chapter(11020000, "第169章 失踪"));
		list.add(new Chapter(11020034, "第170章 睡得好不好"));
		list.add(new Chapter(11024952, "第171章 不要丢下我"));
		list.add(new Chapter(11035164, "第172章 只有他才做得出来"));
		list.add(new Chapter(11036209, "第173章 不要和他成为敌人"));
		list.add(new Chapter(11042545, "第174章 她们是一伙的"));
		list.add(new Chapter(11045957, "第175章 我相信"));
		list.add(new Chapter(11051455, "第176章 好，劫车"));
		list.add(new Chapter(11054602, "第177章 老天爷很闲"));
		list.add(new Chapter(11055680, "第178章 别害羞了"));
		list.add(new Chapter(11062173, "第179章 别再哄我"));
		list.add(new Chapter(11064667, "第180章 月黑风高"));
		list.add(new Chapter(11070453, "第181章 女侠饶命"));
		list.add(new Chapter(11071824, "第182章 风过无声"));
		list.add(new Chapter(11071891, "第183章 出逃后遗症"));
		list.add(new Chapter(11075707, "第184章 还是很喜欢他"));
		list.add(new Chapter(11084112, "第185章 没那个胆子"));
		list.add(new Chapter(11084183, "第186章 长嫂如母"));
		list.add(new Chapter(11087629, "第187章 动静太大"));
		list.add(new Chapter(11087769, "第188章 玄石床"));
		list.add(new Chapter(11103938, "第189章 神奇的力量"));
		list.add(new Chapter(11104206, "第190章 她去哪了"));
		list.add(new Chapter(11111368, "第191章 玄灵境的幻象"));
		list.add(new Chapter(11112718, "第192章 子虚乌有"));
		list.add(new Chapter(11112770, "第193章 先对谁出手"));
		list.add(new Chapter(11121881, "第194章 我们谈谈"));
		list.add(new Chapter(11121982, "第195章 你敢带上武器"));
		list.add(new Chapter(11124836, "第196章 做我女朋友"));
		list.add(new Chapter(11131141, "第197章 想逃？"));
		list.add(new Chapter(11131348, "第198章 想聊天还是想做"));
		list.add(new Chapter(11140715, "第199章 你说过的"));
		list.add(new Chapter(11140797, "第200章 我们不要孩子"));
		list.add(new Chapter(11141036, "第201章 如果没那么怕你"));
		list.add(new Chapter(11153785, "第202章 坦白从宽"));
		list.add(new Chapter(11153904, "第203章 月光令在哪"));
		list.add(new Chapter(11153936, "第204章 几声尖叫"));
		list.add(new Chapter(11165717, "第205章 巨狼"));
		list.add(new Chapter(11165771, "第206章 中毒"));
		list.add(new Chapter(11165831, "第207章 用生命来宠溺"));
		list.add(new Chapter(11165896, "第208章 学着照顾他"));
		list.add(new Chapter(11165984, "第209章 剥提子给你吃"));
		list.add(new Chapter(11166035, "第210章 不可以"));
		list.add(new Chapter(11180389, "第211章 等她醒了再说"));
		list.add(new Chapter(11180546, "第212章 背后的势力"));
		list.add(new Chapter(11180645, "第213章 当年"));
		list.add(new Chapter(11180726, "第214章 孰真孰假"));
		list.add(new Chapter(11192120, "第215章 阿谨，小心"));
		list.add(new Chapter(11192374, "第216章 是不是太顺利了"));
		list.add(new Chapter(11200873, "第217章 井底的秘密"));
		list.add(new Chapter(11200935, "第218章 请君入瓮"));
		list.add(new Chapter(11202160, "第219章 瓮中捉鳖"));
		list.add(new Chapter(11202213, "第220章 一个都不会放过"));
		list.add(new Chapter(11204095, "第221章 只有莫名可以"));
		list.add(new Chapter(11241419, "第222章 究竟在怕什么"));
		list.add(new Chapter(11241551, "第223章 杀人游戏（一）"));
		list.add(new Chapter(11241554, "第224章 杀人游戏（二）"));
		list.add(new Chapter(11241689, "第225章 他是她的天"));
		list.add(new Chapter(11241764, "第226章 想在哪里"));
		list.add(new Chapter(11241827, "第227章 镜子里的两人"));
		list.add(new Chapter(11278251, "第228章 我唯一的女人"));
		list.add(new Chapter(11283511, "第229章 他们是情侣？"));
		list.add(new Chapter(11283579, "第230章 人言可畏"));
		list.add(new Chapter(11283654, "第231章 一丝不安"));
		list.add(new Chapter(11283688, "第232章 帮他一个忙"));
		list.add(new Chapter(11312733, "第233章 下井"));
		list.add(new Chapter(11312755, "第234章 浮力"));
		list.add(new Chapter(11313057, "第235章 我饿了"));
		list.add(new Chapter(11313070, "第236章 相信我"));
		list.add(new Chapter(11352220, "第237章 想家了"));
		list.add(new Chapter(11356548, "第238章 两枚指环"));
		list.add(new Chapter(11356619, "第239章 我知错了"));
		list.add(new Chapter(11356671, "第240章 哪里又错了"));
		list.add(new Chapter(11375581, "第241章 锁定位置"));
		list.add(new Chapter(11390839, "第242章 地宫重现"));
		list.add(new Chapter(11391142, "第243章 变故"));
		list.add(new Chapter(11391266, "第244章 疯子"));
		list.add(new Chapter(11391699, "第245章 围捕"));
		list.add(new Chapter(11391803, "第246章 炎皇"));
		list.add(new Chapter(11403673, "第247章 恐惧"));
		list.add(new Chapter(11403684, "第248章 梦碎"));
		list.add(new Chapter(11403688, "第249章 谁的血"));
		list.add(new Chapter(11403696, "第250章 第一份礼物"));
		list.add(new Chapter(11403708, "第251章 第二份礼物"));
		list.add(new Chapter(11419863, "第252章 第三份礼物"));
		list.add(new Chapter(11430477, "第253章 炎皇的诅咒"));
		list.add(new Chapter(11430545, "第254章 这样爱着你"));
		list.add(new Chapter(11434757, "第255章 蚀骨的痛"));
		list.add(new Chapter(11456075, "第256章 今天好冷"));
		list.add(new Chapter(11456230, "第257章 只是想哭"));
		list.add(new Chapter(11473061, "第258章 乘人之危"));
		list.add(new Chapter(11473128, "第259章 新来的帅哥"));
		list.add(new Chapter(11473618, "第260章 师父"));
		list.add(new Chapter(11490797, "第261章 确实不懂事"));
		list.add(new Chapter(11493786, "第262章 陪我回去吗"));
		list.add(new Chapter(11493870, "第263章 不要扔下我"));
		list.add(new Chapter(11493945, "第264章 证明给我看"));
		list.add(new Chapter(11495261, "第265章 别再勾引我"));
		list.add(new Chapter(11498052, "第266章 只有你可以"));
		list.add(new Chapter(11512881, "第267章 错过"));
		list.add(new Chapter(11512941, "第268章 他回来了"));
		list.add(new Chapter(11512998, "第269章 我怕得病"));
		list.add(new Chapter(11513051, "第270章 莫名，救我"));
		list.add(new Chapter(11513104, "第271章 谁曾经碰过你"));
		list.add(new Chapter(11533640, "第272章 因果循环"));
		list.add(new Chapter(11543838, "第273章 不成人样"));
		list.add(new Chapter(11543992, "第274章 是不是一定要用粗"));
		list.add(new Chapter(11544050, "第275章 再叫，当众要了你"));
		list.add(new Chapter(11544219, "第276章 回哪里去"));
		list.add(new Chapter(11576983, "第277章 只要不背叛我"));
		list.add(new Chapter(11577030, "第278章 确实，好香"));
		list.add(new Chapter(11577078, "第279章 他走了"));
		list.add(new Chapter(11577131, "第280章 真的好想他"));
		list.add(new Chapter(11577180, "第281章 大叔救命"));
		list.add(new Chapter(11577240, "第282章 给我戴上耳麦"));
		list.add(new Chapter(11577329, "第283章 原谅我好不好"));
		list.add(new Chapter(11607348, "第284章 只能是我的"));
		list.add(new Chapter(11607440, "第285章 先生很厉害吧"));
		list.add(new Chapter(11612079, "第286章 他只是帮我"));
		list.add(new Chapter(11612354, "第287章 要她忘了全世界"));
		list.add(new Chapter(11619917, "第288章 是你欠他的"));
		list.add(new Chapter(11630060, "第289章 不许任何人觊觎"));
		list.add(new Chapter(11630265, "第290章 是不是讨厌我"));
		list.add(new Chapter(11635632, "第291章 不放"));
		list.add(new Chapter(11639387, "第292章 为什么是你"));
		list.add(new Chapter(11651795, "第293章 桃花深处"));
		list.add(new Chapter(11657013, "第294章 把他打趴就可以回家"));
		list.add(new Chapter(11667557, "第295章 篝火晚会"));
		list.add(new Chapter(11667654, "第296章 要谁的答案"));
		list.add(new Chapter(11679239, "第297章 我不会放弃你"));
		list.add(new Chapter(11686272, "第298章 再见莫名"));
		list.add(new Chapter(11686423, "第299章 我们不可以"));
		list.add(new Chapter(11686513, "第300章 现在这里很痛"));
		list.add(new Chapter(11699701, "第301章 冰窖中焚烧"));
		list.add(new Chapter(11725103, "第302章 带回来的女孩"));
		list.add(new Chapter(11725276, "第303章 不是你的工具"));
		list.add(new Chapter(11725431, "第304章 你脏，脏死了"));
		list.add(new Chapter(11737727, "第305章 不高兴就打我"));
		list.add(new Chapter(11745941, "第306章 让你发泄"));
		list.add(new Chapter(11746488, "第307章 想回家吗"));
		list.add(new Chapter(11748877, "第308章 你没有权利"));
		list.add(new Chapter(11748992, "第309章 把他的眼睛送给你"));
		list.add(new Chapter(11762899, "第310章 还是这么调皮"));
		list.add(new Chapter(11769861, "第311章 想去？"));
		list.add(new Chapter(11788671, "第312章 三角关系"));
		list.add(new Chapter(11793298, "第313章 徒步旅行01"));
		list.add(new Chapter(11794096, "第314章 徒步旅行02"));
		list.add(new Chapter(11799850, "第315章 徒步旅行03"));
		list.add(new Chapter(11803347, "第316章 徒步旅行04"));
		list.add(new Chapter(11811035, "第317章 徒步旅行05"));
		list.add(new Chapter(11811917, "第318章 徒步旅行06"));
		list.add(new Chapter(11813360, "第319章 徒步旅行07"));
		list.add(new Chapter(11822433, "第320章 徒步旅行08"));
		list.add(new Chapter(11822450, "第321章 徒步旅行09"));
		list.add(new Chapter(11833672, "第322章 徒步旅行10"));
		list.add(new Chapter(11833733, "第323章 徒步旅行11"));
		list.add(new Chapter(11847550, "第324章 不安"));
		list.add(new Chapter(11853081, "第325章 孩子是谁的"));
		list.add(new Chapter(11858490, "第326章 下个月结婚吧"));
		list.add(new Chapter(11860258, "第327章 抓不住的天意"));
		list.add(new Chapter(11860492, "第328章 为宝宝购物"));
		list.add(new Chapter(11871762, "第329章 怕你有意外"));
		list.add(new Chapter(11871844, "第330章 不想你难受"));
		list.add(new Chapter(11872202, "第331章 唯一深爱"));
		list.add(new Chapter(11884836, "第332章 受伤"));
		list.add(new Chapter(11888972, "第333章 明天好不好"));
		list.add(new Chapter(11891108, "第334章 肚子疼"));
		list.add(new Chapter(11894654, "第335章 我要找他"));
		list.add(new Chapter(11894805, "第336章 我没有"));

		list.setCurPageNo(pageNo);
		list.setTotalPageCount((list.size() - 1) / pageItemCount + 1);
		list.setPageItemCount(pageItemCount);
		list.setTotalItemCount(list.size());

		int startIndex = (pageNo - 1) * pageItemCount;
		for (int i = 0; i < startIndex; i++) {
			list.remove(0);
		}
		while (pageItemCount < list.size()) {
			list.remove(pageItemCount);
		}

		return list;
	}

}
