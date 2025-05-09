package com.wskh.enums;

public enum AlgoEnum {
    ;

    public enum PhaseOne {
        GOA("GOA");

        /**
         * 算法名称
         */
        private String name;

        private PhaseOne(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }


    public enum PhaseTwo {
        //近似算法
        SAEA("JinSi", "SAEA"),
        EASA("JinSi", "EASA"),
        ;

        /**
         * 分类名称
         */
        private String categoryName;
        /**
         * 算法名称
         */
        private String name;

        private PhaseTwo(String categoryName, String name) {
            this.categoryName = categoryName;
            this.name = name;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public String getName() {
            return name;
        }

    }

}