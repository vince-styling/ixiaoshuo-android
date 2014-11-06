package com.vincestyling.ixiaoshuo.net.request;

import com.duowan.mobile.netroid.Listener;
import com.vincestyling.ixiaoshuo.net.Respond;
import com.vincestyling.ixiaoshuo.pojo.Category;
import org.codehaus.jackson.type.TypeReference;

import java.util.List;

public class CategoriesRequest extends NetworkRequest<List<Category>> {

    public CategoriesRequest(String url, Listener<List<Category>> listener) {
        super(url, listener);
    }

    @Override
    protected List<Category> convert(Respond respond) {
        List<Category> categoryList = respond.convert(new TypeReference<List<Category>>() {});
        if (categoryList == null || categoryList.size() == 0) return null;
        return categoryList;
    }

}
