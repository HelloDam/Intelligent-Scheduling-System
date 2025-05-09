export default {

    /**
     * 自适应表格列宽
     * @param {字段名名称} fieldName 
     * @param {数据集合} data 
     * @param {展开项id} expandedItems 
     * @param {适应类型} flag 
     * @returns 
     */
    flexColumnWidth(fieldName, data, expandedItems, flag = 'max') {
        // fieldName 为该列的字段名(传字符串);tableData为该表格的数据源(传变量);
        // flag为可选值，可不传该参数,传参时可选'max'或'equal',默认为'max'
        // flag为'max'则设置列宽适配该列中最长的内容,flag为'equal'则设置列宽适配该列中第一行内容的长度。
        fieldName = fieldName + ''
        let columnContent = ''
        if (!data || !data.length || data.length === 0 || data === undefined) {
            return '80px' // 给个默认的
        }
        if (!fieldName || !fieldName.length || fieldName.length === 0 || fieldName === undefined) {
            return '80px' // 给个默认的
        }
        if (flag === 'equal') {
            // 获取该列中第一个不为空的数据(内容)
            for (let i = 0; i < data.length; i++) {
                if (data[i][fieldName].length > 0) {
                    columnContent = data[i][fieldName]
                    break;
                }
            }
        } else {
            // 获取该列中最长的数据(内容)
            for (let i = 0; i < data.length; i++) {
                if (data[i][fieldName] === null) {
                    break;
                }
                //获取该列的最大宽度的内容
                let curContent = this.getMaxContentIncludeChildren(data[i], fieldName, expandedItems);
                if (this.calculateContentWidth(curContent) > this.calculateContentWidth(columnContent)) {
                    columnContent = curContent;
                }
            }
        }
        // 以下分配的单位长度可根据实际需求进行调整
        let flexWidth = this.calculateContentWidth(columnContent);
        if (flexWidth < 80) {
            // 设置最小宽度
            flexWidth = 80
        }
        // if (flexWidth > 250) {
        //   // 设置最大宽度
        //   flexWidth = 250
        // }

        // 可以再多留部分的padding
        flexWidth += 10
        // console.log("flexWidth:" + flexWidth)
        return flexWidth + 'px'
    },
    //获取一项的最大长度的内容（包括children）
    getMaxContentIncludeChildren(item, fieldName, expandedItems) {
        let maxContent = item[fieldName];
        let maxContentWidth = this.calculateContentWidth(maxContent);
        if (item.children != null) {
            let isExpand = false;
            for (let i = 0; i < expandedItems.length; i++) {
                if (expandedItems[i] == item.id) {
                    isExpand = true;
                    break;
                }
            }
            if (isExpand == true) {
                for (let i = 0; i < item.children.length; i++) {
                    let childrenItem = item.children[i];
                    let content = this.getMaxContentIncludeChildren(childrenItem, fieldName);
                    let curWidth = this.calculateContentWidth(content);
                    if (curWidth > maxContentWidth) {
                        maxContent = content;
                    }
                }
            }
        }
        return maxContent;
    },
    //计算内容宽度
    calculateContentWidth(content) {
        let flexWidth = 0
        for (const char of content) {
            if ((char >= 'A' && char <= 'Z') || (char >= 'a' && char <= 'z')) {
                // 如果是英文字符，为字符分配8个单位宽度
                flexWidth += 10
            } else if (char >= '\u4e00' && char <= '\u9fa5') {
                // 如果是中文字符，为字符分配15个单位宽度
                flexWidth += 16
            } else {
                // 其他种类字符，为字符分配8个单位宽度
                flexWidth += 8
            }
        }
        return flexWidth;
    }
}