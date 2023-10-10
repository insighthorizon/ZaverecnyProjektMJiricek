package mjiricek.spring.controllers;

import mjiricek.spring.models.DBEntity;

import java.util.ArrayList;

/**
 * this class reprezents a page for pagination
 */
public class ViewPage {
    /**
     * contents displayed in the page
     */
    private ArrayList<DBEntity> pageContent;

    /**
     * index of the page being displayed
     */
    private int pageIndex;

    /**
     * total number of pages that can be browsed
     */
    private int totalPages;

    /**
     * constructor
     * @param pageContent
     * @param pageIndex
     * @param totalPages
     */
    public ViewPage(ArrayList<DBEntity> pageContent, int pageIndex, int totalPages) {
        this.pageContent = pageContent;
        this.pageIndex = pageIndex;
        this.totalPages = totalPages;
    }

    public ArrayList<DBEntity> getPageContent() {
        return pageContent;
    }

    public void setPageContent(ArrayList<DBEntity> pageContent) {
        this.pageContent = pageContent;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

}
