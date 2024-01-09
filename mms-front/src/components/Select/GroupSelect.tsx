import React, {useEffect, useState} from 'react';
import {Badge, Select, Typography} from "@arco-design/web-react";
import groupStyles from './style/group.module.less';
import listStyles from './style/list.module.less';
import {GroupInfo} from "@/components/Select/type/GroupSelectData";
import cs from "classnames";
import {useGroupSelectData} from "@/components/Select/hooks/useGroupSelect";
import {GroupSelectProps} from "@/components/Select/type/GroupSelectProps";
import {SelectItemData} from "@/components/Select/type/SelectItemData";

const GroupSelect = (props: GroupSelectProps) => {
  const { options, mode = 'single', onChange, resetSingle, children } = props;
  const singleMode = mode === 'single';
  const multipleMode = mode === 'multiple';

  const { selectData, getSelectedGroupIndex, getSelectedItemIndex, isSelected,
    addNewGroupSelect, addSelect, removeSelect, cleanGroupSelect, clearSelected,
  } = useGroupSelectData([]);

  const [ popupVisible, setPopupVisible ] = useState<boolean>(false);
  const [ resetSingleInner, setResetSingleInner ] = useState<number>(0);

  useEffect(() => onChange && onChange(selectData), [selectData])

  useEffect(() => {
    if (resetSingle !== resetSingleInner) {
      if (selectData && selectData.length > 0) {
        clearSelected();
      }
      setResetSingleInner(resetSingle)
    }
  }, [resetSingle])

  function onSelect(groupInfo: GroupInfo, selectItem: SelectItemData) {
    const selectedGroupIndex = getSelectedGroupIndex(groupInfo.code);
    if (selectedGroupIndex === -1) {
      addNewGroupSelect(groupInfo, selectItem);
      return;
    }
    const selectedGroup = selectData[selectedGroupIndex];
    const selectedIndex = getSelectedItemIndex(selectItem.value, selectedGroup.items);
    if (selectedIndex === -1) {
      if (singleMode) {
        cleanGroupSelect(selectedGroupIndex);
      }
      addSelect(groupInfo, selectItem);
    } else if (multipleMode){
      removeSelect(selectedGroupIndex, selectedIndex);
    }
  }

  return (
    <Select
      popupVisible={popupVisible}
      triggerProps={{
        autoAlignPopupWidth: false,
        autoAlignPopupMinWidth: true,
        position: 'bottom',
      }}
      triggerElement={(
        <Badge count={selectData.length} dot dotStyle={{ width: 6, height: 6 }}>
          {children}
        </Badge>
      )}
      onVisibleChange={setPopupVisible}
      dropdownRender={() => (
        <div className={groupStyles['group']}>
          { options.map((option, index) => (
            <div key={index} className={groupStyles['group-item-outer']}>
              <Typography.Text className={groupStyles['group-item-name']}>
                {option.groupInfo.name}
              </Typography.Text>
              { option.items.map((item, index) => (
                <div key={index} className={cs(listStyles['list-item'],
                  {[listStyles['list-item-active']]: isSelected(option.groupInfo.code, item.value)})}
                     onClick={() => {
                       onSelect(option.groupInfo, item);
                       setPopupVisible(false);
                     }}>
                  <div className={listStyles['list-item-icon']}>{item.icon}</div>
                  <Typography.Text className={listStyles['list-item-name']}>
                    {item.name}
                  </Typography.Text>
                </div>
              ))}
            </div>
          ))}
        </div>
      )}
    />
  )
}

export default GroupSelect;
