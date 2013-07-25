package com.duowan.mobile.ixiaoshuo.pojo;

import java.util.ArrayList;
import java.util.List;

public class Book {
	private int id;
	private int websiteId;
	private String name;
	private String author;
	private String category;
	private String coverFileName;
	private String coverUrl;
	private String summary;
	private String lastUpdateTime;
	private int readerCount;

	private int updateStatus;
	public static final int STATUS_CONTINUE = 1; // 1：连载
	public static final int STATUS_FINISHED = 2; // 2：完结

	public static final String RANK_WEEK	= "week";
	public static final String RANK_MONTH	= "month";
	public static final String RANK_TOTAL	= "total";

	private List<Chapter> chapterList;

	public Book() {}
	public Book(int id, String name, String author, String coverFileName) {
		this.id = id;
		this.name = name;
		this.author = author;
		this.coverFileName = coverFileName;
	}

	public List<Chapter> getChapterList() {
		return chapterList;
	}

	public void setChapterList(List<Chapter> chapterList) {
		this.chapterList = chapterList;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setBookId(int bookId) {
		this.id = bookId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getCoverFileName() {
		return coverFileName;
	}

	public void setCoverFileName(String coverFileName) {
		this.coverFileName = coverFileName;
	}

	public String getUpdateStatusStr() {
		return updateStatus == 1 ? "连载" : "完结";
	}

	public int getUpdateStatus() {
		return updateStatus;
	}

	public void setUpdateStatus(int updateStatus) {
		this.updateStatus = updateStatus;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCoverUrl() {
		return coverUrl;
	}

	public void setCoverUrl(String coverUrl) {
		this.coverUrl = coverUrl;
	}

	public String getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(String lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public int getWebsiteId() {
		return websiteId;
	}

	public void setWebsiteId(int websiteId) {
		this.websiteId = websiteId;
	}

	public int getReaderCount() {
		return readerCount;
	}

	public void setReaderCount(int readerCount) {
		this.readerCount = readerCount;
	}

	public static List<Book> getStaticBookList() {
		List<Book> mBookList = new ArrayList<Book>(50);
		mBookList.add(new Book(582544, "纵横妖逆", "驳筝", "cover_582544.jpg"));
		mBookList.add(new Book(588911, "纵横妖逆第二部", "驳筝之一", "cover_588911.png"));
		mBookList.add(new Book(465602, "残袍", "风御九秋", "cover_465602.jpg"));
		mBookList.add(new Book(395390, "踏霄录", "桓僧", "cover_395390.jpg"));
		mBookList.add(new Book(488766, "我的极品女友们", "超级大坦克科比", "cover_488766.jpg"));
		mBookList.add(new Book(412271, "逆天九鼎", "斜阳红尘", "cover_412271.jpg"));
		mBookList.add(new Book(513563, "巅峰圣祖", "神枫兵少", "cover_513563.jpg"));
		mBookList.add(new Book(441624, "我才不会被女孩子欺负呢", "废铁行者", "cover_441624.jpg"));
		mBookList.add(new Book(486126, "醉掌星辰", "苍穹一少", "cover_486126.jpg"));
		mBookList.add(new Book(471287, "斩龙", "失落叶", "cover_471287.jpg"));
		mBookList.add(new Book(392518, "最终逆战", "希言菲语", "cover_392518.jpg"));
		mBookList.add(new Book(576273, "校花的贴身狂龙", "纯洁的黑狼", "cover_576273.jpg"));
		mBookList.add(new Book(587976, "名门诱宠", "微风中摇曳", "cover_587976.jpg"));
		mBookList.add(new Book(439157, "官路红颜", "江南活水", "cover_439157.jpg"));
		mBookList.add(new Book(483851, "我的狐仙老婆", "黑夜de白羊", "cover_483851.jpg"));
		mBookList.add(new Book(498164, "诡歌", "忆珂梦惜", "cover_498164.jpg"));
		mBookList.add(new Book(556155, "与美女总裁同居的日子", "两个大馒头", "cover_556155.jpg"));
		mBookList.add(new Book(492248, "匹夫的逆袭", "骁骑校", "cover_492248.jpg"));
		mBookList.add(new Book(562508, "无限军火库", "见血封侯", "cover_562508.jpg"));
		mBookList.add(new Book(480820, "网游之诛神重生", "李小猫", "cover_480820.jpg"));
		mBookList.add(new Book(530299, "武动苍冥", "落叶无言", "cover_530299.jpg"));
		mBookList.add(new Book(486042, "暗之极", "明珠万斛", "cover_486042.jpg"));
		mBookList.add(new Book(468750, "网游之王者无敌", "孤雨随风", "cover_468750.jpg"));
		mBookList.add(new Book(575146, "雷破武空", "东方小艺", "cover_575146.jpg"));
		mBookList.add(new Book(489281, "桃运官途", "小楼昨夜轻风", "cover_489281.jpg"));
		mBookList.add(new Book(339612, "网游之霸王传说", "名楚", "cover_339612.jpg"));
		mBookList.add(new Book(433120, "网游之江山美人", "江山与美人", "cover_433120.jpg"));
		mBookList.add(new Book(496417, "卖萌凤仙住我家", "都果儿", "cover_496417.jpg"));
		mBookList.add(new Book(591298, "花心保镖", "笑看雪舞", "cover_591298.jpg"));
		mBookList.add(new Book(493239, "修罗武神", "善良的蜜蜂", "cover_493239.jpg"));
		mBookList.add(new Book(507544, "开艘航母去抗日", "且听沧海", "cover_507544.jpg"));
		mBookList.add(new Book(315721, "笑揽美人回人间", "达士", "cover_315721.jpg"));
		mBookList.add(new Book(588354, "殇天荡地", "微风习习", "cover_588354.jpg"));
		mBookList.add(new Book(143095, "特种教师", "黑暗崛起", "cover_143095.jpg"));
		mBookList.add(new Book(467707, "灵舟", "九当家", "cover_467707.jpg"));
		mBookList.add(new Book(537431, "至尊战士", "资深小狐狸", "cover_537431.jpg"));
		mBookList.add(new Book(569749, "与狐仙双修的日子", "美女请自重", "cover_569749.jpg"));
		mBookList.add(new Book(591565, "美女别发烧", "糊吹", "cover_591565.jpg"));
		mBookList.add(new Book(391013, "龙血战神", "风青阳", "cover_391013.jpg"));
		mBookList.add(new Book(128633, "纨绔才子混都市", "sr宝贝", "cover_128633.jpg"));
		mBookList.add(new Book(374417, "剑皇重生", "血舞天", "cover_374417.jpg"));
		mBookList.add(new Book(417224, "天逆", "陈辉", "cover_417224.jpg"));
		mBookList.add(new Book(347853, "官道", "温岭闲人", "cover_347853.jpg"));
		mBookList.add(new Book(512159, "腹黑小兔硬上攻", "离薇", "cover_512159.jpg"));
		mBookList.add(new Book(486334, "皇陵宝藏", "畅销书王", "cover_486334.jpg"));
		mBookList.add(new Book(438776, "媚惑倾城", "五月锶", "cover_438776.jpg"));
		mBookList.add(new Book(589795, "逆神论之幻恋", "吴狼哲", "cover_589795.jpg"));
		mBookList.add(new Book(584273, "最后的猎魔人", "符咒祝由师贾树", "cover_584273.jpg"));
		mBookList.add(new Book(499248, "天空魔法师", "谈笑醉红尘", "cover_499248.jpg"));
		mBookList.add(new Book(549148, "炼神", "流牙", "cover_549148.jpg"));
		mBookList.add(new Book(546048, "人王", "若安息", "cover_546048.jpg"));
		mBookList.add(new Book(507058, "冒牌大昏君", "码字小神", "cover_507058.jpg"));
		mBookList.add(new Book(493865, "邪魅王爷淘气妃", "灵狐缘梦", "cover_493865.jpg"));
		mBookList.add(new Book(512705, "至尊兵王", "卓公子", "cover_512705.jpg"));
		mBookList.add(new Book(588659, "极道狂仙", "红苕炖地瓜", "cover_588659.jpg"));
		mBookList.add(new Book(525359, "采花小狐哪里逃", "兔子阿银", "cover_525359.jpg"));
		mBookList.add(new Book(433760, "墓地封印", "一叶style", "cover_433760.jpg"));
		mBookList.add(new Book(446704, "我们是兄弟", "纯银耳坠", "cover_446704.jpg"));
		mBookList.add(new Book(582529, "蛇吻拽妃", "萧宠儿", "cover_582529.jpg"));
		mBookList.add(new Book(526247, "撒旦缠情：老婆，宠你上瘾", "绯若烟", "cover_526247.jpg"));
		mBookList.add(new Book(573746, "凤色天下", "冷月凝云", "cover_573746.jpg"));
		mBookList.add(new Book(566327, "花心小哥俏皮妹", "暮归", "cover_566327.jpg"));
		mBookList.add(new Book(488019, "与苍老师同居的日子", "大大湿胸", "cover_488019.jpg"));
		mBookList.add(new Book(496219, "王爷在上", "浅浅的笑", "cover_496219.jpg"));
		mBookList.add(new Book(584427, "美女俺来了", "成鹏", "cover_584427.jpg"));
		mBookList.add(new Book(460553, "神环啸", "商朝雨", "cover_460553.jpg"));
		mBookList.add(new Book(589314, "武魂", "枫落忆痕", "cover_589314.jpg"));
		mBookList.add(new Book(575839, "重灵", "契约书徒", "cover_575839.jpg"));
		mBookList.add(new Book(585374, "烽火燃魂", "淡淡墨色", "cover_585374.jpg"));
		mBookList.add(new Book(417229, "官场桃花运", "北岸", "cover_417229.jpg"));
		return mBookList;
	}

}
