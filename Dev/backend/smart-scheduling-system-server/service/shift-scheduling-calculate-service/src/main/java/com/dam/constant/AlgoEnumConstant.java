package com.dam.constant;

import com.dam.model.vo.scheduling_calculate_service.AlgorithmGroupVo;
import com.wskh.enums.AlgoEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlgoEnumConstant {

    /**
     * 第一阶段算法选项
     */
    public static final AlgoEnum.PhaseOne[] phaseOneArr = new AlgoEnum.PhaseOne[]{
//            AlgoEnum.PhaseOne.BP,
//            AlgoEnum.PhaseOne.CG,
            AlgoEnum.PhaseOne.GOA,
//            AlgoEnum.PhaseOne.SC
    };
    /**
     * 第二阶段算法选项
     */
    public static final AlgoEnum.PhaseTwo[] phaseTwoArr = new AlgoEnum.PhaseTwo[]{
//            AlgoEnum.PhaseTwo.AGA,
//                AlgoEnum.PhaseTwo.CG,
            AlgoEnum.PhaseTwo.SAEA,
            AlgoEnum.PhaseTwo.EASA,
//            AlgoEnum.PhaseTwo.ALNS,
//            AlgoEnum.PhaseTwo.TS,
//            AlgoEnum.PhaseTwo.GMMA,
//            AlgoEnum.PhaseTwo.ILS,
//            AlgoEnum.PhaseTwo.SA,
//            AlgoEnum.PhaseTwo.VNS
    };

    public static final String splitStr = " - ";

    /**
     * 存储算法名称 及其对应的 枚举
     */
    public static Map<String, AlgoEnum.PhaseOne> nameAndPhaseOneMap = new HashMap<>();
    public static Map<String, AlgoEnum.PhaseTwo> nameAndPhaseTwoMap = new HashMap<>();

    public static List<AlgorithmGroupVo> algorithmGroupVoList = new ArrayList<>();
    public static List<String> algorithmGroupStrList = new ArrayList<>();

    //只要使用到这个类的变量，static里面的代码就会执行
    static {
//        System.out.println("AlgoEnumConstant类初始化");
        for (int i = 0; i < phaseOneArr.length; i++) {
            AlgoEnum.PhaseOne phaseOne = phaseOneArr[i];
            nameAndPhaseOneMap.put(phaseOne.getName(), phaseOne);
        }

        for (int i = 0; i < phaseTwoArr.length; i++) {
            AlgoEnum.PhaseTwo phaseTwo = phaseTwoArr[i];
            nameAndPhaseTwoMap.put(phaseTwo.getName(), phaseTwo);
        }

        for (int i = 0; i < phaseOneArr.length; i++) {
            for (int j = 0; j < phaseTwoArr.length; j++) {
//                algorithmGroupVoList.add(new AlgorithmGroupVo(phaseOneArr[i].getName(), phaseTwoArr[j].getName()));
                algorithmGroupStrList.add(phaseOneArr[i].getName() + splitStr + phaseTwoArr[j].getName());
            }
        }
    }
}
