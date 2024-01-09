package com.epam.course;

import lombok.Getter;
import lombok.Setter;

/**
    Give statistic to user:
    Average time spent for putting new values into cache
    Number of cache evictions
*/
@Getter
@Setter
public class Statistics {
    private long averageInsertTime;
    private int numberOfCacheEvictions;

}


