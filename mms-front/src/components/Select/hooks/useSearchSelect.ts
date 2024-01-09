import {useState} from "react";
import {SelectItemData} from "@/components/Select/type/SelectItemData";

export function useSearchSelect(initialData: SelectItemData[] | string[] = []) {
  const [selectData, setSelectData] = useState<SelectItemData[] | string[]>(initialData);

  function isStringMode(value: SelectItemData | string) {
    return typeof value === 'string';
  }

  function getSelectedIndex(value: SelectItemData | string): number {
    if (!value) {
      return -1;
    }
    const isString = isStringMode(value);

    return isString ?
      selectData.findIndex(item => item === value) :
      selectData.findIndex(item => item.value === (value as SelectItemData).value);
  }

  function isSelected(value: SelectItemData | string) {
    if (selectData && selectData.length === 0) {
      return false;
    }
    const itemIndex = getSelectedIndex(value);
    return itemIndex !== -1;
  }

  function addSelect(selectItem: SelectItemData | string) {
    const isString = isStringMode(selectItem);
    const itemIndex = getSelectedIndex(selectItem);

    setSelectData(prevState => {
      const newSelectData: (SelectItemData | string)[] = [...prevState];

      if (itemIndex === -1) {
        newSelectData.push(selectItem);
      } else {
        newSelectData[itemIndex] = selectItem;
      }

      if (isString) {
        return newSelectData as string[];
      } else {
        return newSelectData as SelectItemData[];
      }
    });
  }

  function removeSelect(selectItem: SelectItemData | string) {
    if (!selectItem) {
      return;
    }

    const isString = isStringMode(selectItem);

    setSelectData(prevState => {
      const newSelectData: (SelectItemData | string)[] = [...prevState];

      const itemIndex = getSelectedIndex(selectItem);
      newSelectData.splice(itemIndex, 1);

      if (isString) {
        return newSelectData as string[];
      } else {
        return newSelectData as SelectItemData[];
      }
    });
  }

  function clearSelected() {
    setSelectData([]);
  }

  return {
    selectData,
    isSelected,
    addSelect,
    removeSelect,
    clearSelected,
  };
}
