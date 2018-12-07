package com.damon.father;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FatherClass<T> {

   /* public List<T> deleteDuplication(List<T> list){
        if (list == null || list.size() == 0) return list;
        List<T> newList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (newList.contains(list.get(i))) continue;
            newList.add(list.get(i));
        }
        return newList;
    }*/

    /**
     * 删除重复项
     * @param list
     * @return
     */
   public Set<T> deleteDuplication(List<T> list){
       if (list == null || list.size() == 0) return null;
       Set<T> set = new HashSet<>();
       for (int i = 0; i < list.size(); i++) {
           if (set.contains(list.get(i))) continue;
           set.add(list.get(i));
       }
       return set;
   }

    /**
     * 找出重复的数据
     * @param list
     * @return
     */
   public List<T> findDuplication(List<T> list){
       if (list == null || list.size() == 0) return null;
       List<T> finalList = new ArrayList<>();
       Set<T> set = new HashSet<>();
       for (int i = 0; i < list.size(); i++) {
           //如果set中含有这条数据，那就把这条数据添加到List中
           if (set.contains(list.get(i))) {
               finalList.add(list.get(i));
               continue;
           }
           set.add(list.get(i));
       }
       return finalList;
   }

}
