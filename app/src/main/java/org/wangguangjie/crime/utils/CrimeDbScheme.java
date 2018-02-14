package org.wangguangjie.crime.utils;

/**
 * Created by wangguangjie on 2017/10/23.
 */

public class CrimeDbScheme {

    public static final class CrimeTable{
        public static final String NAME="crime";
        public static final class Cols{
            public static final String UUID="uuid";
            public static final String TITLE="title";
            public static final String DATE="date";
            public static final String SOLVED="solved";
            public static final String SUSPECT="suspect";
            public static final String PHONENUMBER="phoneNumber";
        }
    }
}
