import {useState} from "react";
import {GroupInfo, GroupSelectData} from "@/components/Select/type/GroupSelectData";
import {SelectItemData} from "@/components/Select/type/SelectItemData";

export function useGroupSelectData(initialData: GroupSelectData[] = []) {
  const [selectData, setSelectData] = useState<GroupSelectData[]>(initialData);

  function getSelectedGroup(groupCode: string): GroupSelectData {
    const selectedGroupIndex = getSelectedGroupIndex(groupCode);
    if (-1 === selectedGroupIndex) {
      return undefined;
    }
    return selectData[selectedGroupIndex];
  }

  function getSelectedGroupIndex(groupCode: string): number {
    if (!groupCode) {
      return -1;
    }
    return selectData.findIndex(item => item.groupInfo.code === groupCode);
  }

  function getSelectedItem(value, items: SelectItemData[]): SelectItemData {
    const selectedItemIndex = getSelectedItemIndex(value, items);
    if (-1 === selectedItemIndex) {
      return undefined;
    }
    return items[selectedItemIndex];
  }

  function getSelectedItemIndex(value, items: SelectItemData[]): number {
    if (!value) {
      return -1;
    }
    return items.findIndex(item => item.value === value);
  }

  function isSelected(groupName: string, value: string) {
    if (!groupName || !value) { return false; }
    const selectedGroupData = getSelectedGroup(groupName);
    if (!selectedGroupData) {
      return false;
    }
    return selectedGroupData.items.findIndex(item => item.value === value) !== -1;
  }

  function addNewGroupSelect(groupInfo: GroupInfo, selectItem: SelectItemData) {
    setSelectData(prevState => {
      const newArr = [ ...prevState ];
      const selectGroupData:GroupSelectData  = { groupInfo, items: [selectItem] };
      newArr.push(selectGroupData);
      return newArr;
    });
  }

  function addSelect(groupInfo: GroupInfo, selectItem: SelectItemData) {
    const groupIndex = getSelectedGroupIndex(groupInfo.code);
    setSelectData(prevState => {
      const newArr = [ ...prevState ];
      newArr[groupIndex].items.push(selectItem);
      const selectGroupData:GroupSelectData  = { groupInfo, items: [selectItem] };
      newArr.push(selectGroupData);
      return newArr;
    });
  }

  function removeSelect(groupIndex, selectItemIndex) {
    setSelectData(prevState => {
      const newArr = [ ...prevState ];
      newArr[groupIndex].items.splice(selectItemIndex, 1);
      return newArr;
    });
  }

  function cleanGroupSelect(selectedGroupIndex: number) {
    setSelectData(prevState => {
      const newArr = [ ...prevState ];
      newArr[selectedGroupIndex].items = [];
      return newArr;
    });
  }

  function clearSelected() {
    setSelectData([]);
  }

  return {
    selectData,
    getSelectedGroup,
    getSelectedGroupIndex,
    getSelectedItem,
    getSelectedItemIndex,
    isSelected,
    addNewGroupSelect,
    addSelect,
    removeSelect,
    cleanGroupSelect,
    clearSelected,
    setSelectData,
  };
}
