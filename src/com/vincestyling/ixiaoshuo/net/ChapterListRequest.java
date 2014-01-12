package com.vincestyling.ixiaoshuo.net;

import com.duowan.mobile.netroid.*;
import com.vincestyling.ixiaoshuo.pojo.Chapter;
import com.vincestyling.ixiaoshuo.utils.AppLog;
import com.vincestyling.ixiaoshuo.utils.PaginationList;

public class ChapterListRequest extends Request<PaginationList<Chapter>> {

	public ChapterListRequest(String url, Response.Listener<PaginationList<Chapter>> listener) {
		super(Method.GET, url, listener);
	}

	@Override
	protected Response<PaginationList<Chapter>> parseNetworkResponse(NetworkResponse response) {
		try {
			String json = new String(response.data, response.charset);
			Respond respond = GObjectMapper.get().readValue(json, Respond.class);
			if (Respond.isCorrect(respond)) {
				PaginationList<Chapter> chapterList = respond.convertPaginationList(Chapter.class);
				return Response.success(chapterList, new Cache.Entry(response.data, response.charset));
			} else {
				return Response.error(new NetroidError(respond.toString()));
			}
		} catch (Exception e) {
			AppLog.e(e);
			return Response.error(new NetroidError(e));
		}
	}

}
