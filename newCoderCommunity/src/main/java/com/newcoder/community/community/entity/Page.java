package com.newcoder.community.community.entity;


/**
 * 封装页面相关的信息
 */
public class Page {

    //当前页码
    private int current = 1;
    //显示上限
    private int limit = 10;
    //数据总数（用于计算总页数）
    private int rows;
    // 查询路径（复用分页的路径）
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current >= 1)
            this.current = current;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit >= 1 && limit <= 100)
            this.limit = limit;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows >= 0)
            this.rows = rows;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取当前页的起始行
     *
     * @return
     */
    public int getOffset() {
        //第一页：开始为0行；第二页：开始为10行，以此类推
        return (current - 1) * limit;
    }

    /**
     * 获取总的页数
     *
     * @return
     */
    public int getTotal() {
        // rows / limit + [1]
        if (rows % limit == 0) {
            return rows / limit;
        } else {
            return rows / limit + 1;
        }
    }

    /**
     * 获取起始页码
     *
     * @return
     */
    public int getFrom() {
        int from = current - 2;
        if (from < 1)
            from = 1;
        return from;
    }


    /**
     * 获取终止页码
     *
     * @return
     */
    public int getTo() {
        int to = current + 2;
        int total = getTotal();
        if (to > total)
            to = total;
        return to;
    }



}
