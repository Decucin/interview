package com.decucin.dataStructure;

public class DuplicationInArray {
    /**
     * @author decucin
     * @descreption
     * 所给的数组中所有的数字都在0 ~ n-1范围内，数组中某些数字是重复的，但是不知道有几个重复，也不知道每个数字重复了几次
     * 请找出并返回任何一个重复的数字
     * @data 17:02 2022/9/12
     * @param nums: 含有重复数字的数组
     * @return: int
     **/
    public static int findFirstDuplicationNumber(int[] nums){
        if(nums == null || nums.length < 1){
            return -1;
        }
        int n = nums.length;
        int index = 0;
        while (index < n){
            // 表示当前位置符合要求，跳过就行
            if(nums[index] == index){
                ++index;
                continue;
            }
            // 健壮性判断
            if(nums[index] < 0 || nums[index] >= n){
                return -1;
            }
            // 下标为index对应数字应该在下标为nums[index]处
            // 若是那个位置的数和现在的数相同，那么说明那里已经放置了一个正确元素，故该元素重复，返回即可
            if(nums[nums[index]] == nums[index]){
                return nums[index];
            }else {
                // 否则交换，继续比较
                swap(nums, index, nums[index]);
            }
        }
        // 到这里说明所有元素都符合要求了
        return  -1;
    }

    /**
     * @author decucin
     * @descreption 交换数组中i, j下标对应的数字
     * @data 17:09 2022/9/12
     * @param nums: 给定数组
     * @param i: 所给的需要交换的第一个下标
     * @param j: 所给的需要交换的第二个下标
     * @return: void
     **/
    private static void swap(int[] nums, int i, int j){
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }

}
