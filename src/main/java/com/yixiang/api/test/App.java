package com.yixiang.api.test;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.Transformer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feilong.core.CharsetType;
import com.feilong.core.DatePattern;
import com.feilong.core.TimeInterval;
import com.feilong.core.Validator;
import com.feilong.core.bean.BeanUtil;
import com.feilong.core.bean.ConvertUtil;
import com.feilong.core.bean.PropertyUtil;
import com.feilong.core.date.DateExtensionUtil;
import com.feilong.core.date.DateUtil;
import com.feilong.core.util.AggregateUtil;
import com.feilong.core.util.CollectionsUtil;
import com.feilong.core.util.MapUtil;
import com.feilong.core.util.RegexUtil;
import com.feilong.core.util.SortUtil;
import com.feilong.core.util.predicate.BeanPredicateUtil;

public class App {
	
	public static void main(String[] args) {
		Set<String> a=new TreeSet<String>();
		System.out.println(Validator.isNullOrEmpty(a));
		System.out.println(DateUtil.toString(new Date(), DatePattern.COMMON_DATE));
		System.out.println(CharsetType.UTF8);
		System.out.println(DateUtil.toDate("2018-04-08", "yyyy","yyyy-MM-dd","MM-dd"));
		
		System.out.println(DateExtensionUtil.formatDuration(DateUtil.toDate("2018-04-07 17:00:00", DatePattern.COMMON_DATE_AND_TIME)));
		System.out.println(DateExtensionUtil.formatDuration(100*TimeInterval.MILLISECOND_PER_SECONDS));
		
		CollectionsUtil.addAllIgnoreNull(a, Arrays.asList("1","2"));
		System.out.println(a);

		List<Map<String,Object>> b=CollectionsUtil.newArrayList();
		Map<String,Object> p=new HashMap<String,Object>();
		p.put("id", 1);
		p.put("name", "a");
		b.add(p);
		p=new HashMap<String,Object>();
		p.put("id", 2);
		p.put("name", "b");
		b.add(p);
		System.out.println(CollectionsUtil.find(b, "id", 3));
		
		Predicate<Map<String,Object>> pd=new Predicate<Map<String,Object>>() {
			
			@Override
			public boolean evaluate(Map<String, Object> arg0) {
				return Integer.valueOf(arg0.get("id").toString())==2;
			}
		};
		System.out.println(CollectionsUtil.find(b, pd));
		
		System.out.println(CollectionsUtil.find(b, BeanPredicateUtil.equalPredicate("id", 1)));
		
		Map<String,Object> map=CollectionsUtil.getPropertyValueMap(b, "id", "name");
		System.out.println(map);
		
		Map<String,List<Map<String,Object>>> m=CollectionsUtil.group(b, "id");
		System.out.println(m);
		
		List<Map<String,Object>> c=CollectionsUtil.remove(b, p);
		System.out.println(c);
		
		List<String> d=Arrays.asList("1","2","1","3","4","4","5");
		System.out.println(CollectionsUtil.removeDuplicate(d));
		
		Transformer<String, Object> tf=new Transformer<String, Object>() {
			@Override
			public Object transform(String arg0) {
				return Integer.valueOf(arg0)+10;
			}
		};
		System.out.println(CollectionsUtil.collect(d, tf));
		
		Map<String,User> p1=MapUtil.newHashMap();
		p1.put("stu1", new User(1,"a",10));
		p1.put("stu2", new User(2,"b",10));
		p1.put("stu3", new User(3,"c",30));
		System.out.println(MapUtil.extractSubMap(p1, "name"));
		
		Map<String,String> p2=MapUtil.newHashMap();
		p2.put("stu1", "a");
		p2.put("stu1", "b");
		p2.put("stu3", "c");
		System.out.println(JSONObject.toJSON(MapUtil.toArrayValueMap(p2)));
		
		System.out.println(JSONObject.toJSON(SortUtil.sortArray(d.toArray())));
		
		List<User> p3=Arrays.asList(new User(1,"a",10),new User(1,"b",20),new User(3,"c",30));
		System.out.println(JSONObject.toJSON(SortUtil.sortListByPropertyNamesValue(p3, "id desc","age")));
		
		System.out.println(JSONArray.toJSON(SortUtil.sortListByFixedOrderPropertyValueArray(p3, "name","c","a","b")));
		
		System.out.println(AggregateUtil.avg(p3, "age", 2));
		
		System.out.println(RegexUtil.matches("\\d+", "abc"));
		
		System.out.println(JSONArray.toJSON(ConvertUtil.toArray(d,String.class)));
		
		DynaBean db=BeanUtil.newDynaBean(ConvertUtil.toMap("id", 1, "name", "奎托斯"));
		System.out.println(JSONObject.toJSON(db));
		
		System.out.println(JSONObject.toJSON(PropertyUtil.describe(p3.get(0),"name")));
		
	}
	
}
