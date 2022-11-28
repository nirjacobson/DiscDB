package com.nirjacobson.discdb.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class FindResultsView {

    @JsonProperty(FieldDefs.RESULTS)
    private List<DiscView> _results;

    @JsonProperty(FieldDefs.PAGES)
    private int _pages;

    public FindResultsView(final List<DiscView> pResults, final int pPages) {
        _results = pResults;
        _pages = pPages;
    }

    public static class FieldDefs {
        public static final String RESULTS = "results";
        public static final String PAGES = "pages";
    }
}
