package org.kesar.lazy.lazydb.core;

import android.database.Cursor;

import org.kesar.lazy.lazydb.util.ObjectUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * select sql的构建器
 * Created by kesar on 2016/6/21 0021.
 */
public class SelectBuilder<T> {
    private final SQLiteDBExecutor executor;

    final Class<T> objectClass;
    String[] columns;
    String whereSection;
    String[] whereArgs;

    String having;
    String orderBy;
    String groupBy;
    String limit;

    public SelectBuilder(SQLiteDBExecutor executor, Class<T> clazz) {
        this.objectClass = clazz;
        this.executor = executor;
    }

    public SelectBuilder<T> selectAll() {
        return this;
    }

    public SelectBuilder<T> select(String... columns) {
        this.columns = columns;
        return this;
    }

    public SelectBuilder<T> where(String whereSection, String... whereArgs) {
        this.whereSection = whereSection;
        this.whereArgs = whereArgs;
        return this;
    }

    public SelectBuilder<T> having(String having) {
        this.having = having;
        return this;
    }

    public SelectBuilder<T> orderBy(String orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    public SelectBuilder<T> groupBy(String groupBy) {
        this.groupBy = groupBy;
        return this;
    }

    public SelectBuilder<T> limit(String limit) {
        this.limit = limit;
        return this;
    }

    /**
     * 执行查询操作，获取查询结果集
     *
     * @return 数据库游标 Cursor
     */
    public Cursor executeNative() {
        // 查询表是否存在
        String sql = SQLBuilder.buildQueryTableIsExistSql(objectClass);
        Cursor cursor = executor.rawQuery(sql, null);
        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {//cursor初始位置是在-1,而数据是从0开始的，所以cursor.moveToNext刚好是从-1变成0，不需要moveToFirst而是直接循环moveToNext就可以完成遍历。
                    if (cursor.getInt(0) == 0) {
                        return null;
                    }
                }
            } finally {
                cursor.close();
            }
        } else {
            return null;
        }

        //String sql = SqlBuilder.buildQuerySql(TableUtil.getTableName(objectClass), columns, whereSection, whereArgs, groupBy, having, orderBy, limit);
        //Cursor cursor = db.rawQuery(sql, null);
        // 执行查询
        return executor.query(this);
    }

    /**
     * 执行查询操作，获取查询结果集
     *
     * @return 查询结果集，空集则查询不到
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     * @throws ParseException
     */
    public List<T> findAll() throws InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException {
        List<T> results = new ArrayList<>();

        // 执行查询
        Cursor cursor = executeNative();
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    T object = ObjectUtil.buildObject(objectClass, cursor);
                    results.add(object);
                }
            } finally {
                cursor.close();
            }
        }
        return results;
    }

    /**
     * 执行查询操作，获取查询结果集的第一个
     *
     * @return 查询结果集的第一个，null则查询不到
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     * @throws ParseException
     */
    public T findFirst() throws InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException {
        T result = null;

        // 执行查询
        Cursor cursor = executeNative();
        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {
                    result = ObjectUtil.buildObject(objectClass, cursor);
                }
            } finally {
                cursor.close();
            }
        }
        return result;
    }
}
