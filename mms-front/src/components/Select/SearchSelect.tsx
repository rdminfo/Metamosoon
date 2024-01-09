import React, {useEffect, useState} from 'react';
import {Divider, Input, Select, Typography} from "@arco-design/web-react";
import {useSearchSelect} from "@/components/Select/hooks/useSearchSelect";
import searchStyles from "./style/search.module.less";
import listStyles from "./style/list.module.less";
import cs from "classnames";
import {SearchSelectProps} from "@/components/Select/type/SearchSelectProps";
import useLocale from "@/utils/useLocale";
import locale from "./local";
import globalLocale from "@/locale";
import IconEmptyCustom from '../../assets/empty.svg';
import {SelectItemData} from "@/components/Select/type/SelectItemData";

const SearchSelect = (props: SearchSelectProps) => {
  const t = useLocale(locale)
  const tg = useLocale(globalLocale);
  const { options, mode = 'single', onChange, resetSingle, position, children } = props;
  const singleMode = mode === 'single';
  const multipleMode = mode === 'multiple';

  const { selectData, isSelected, addSelect, removeSelect, clearSelected } = useSearchSelect([]);

  const [ popupVisible, setPopupVisible ] = useState<boolean>(false);
  const [ resetSingleInner, setResetSingleInner ] = useState<number>(0);

  useEffect(() => {
    if (resetSingle !== resetSingleInner) {
      if (selectData && selectData.length > 0) {
        clearSelected();
      }
      setResetSingleInner(resetSingle)
    }
  }, [resetSingle])

  useEffect(() => onChange && onChange(selectData), [selectData])

  function onSelect(selectItem: SelectItemData | string) {
    const isSelectedBool = isSelected(selectItem);
    if (!isSelectedBool) {
      if (singleMode) {
        clearSelected();
      }
      addSelect(selectItem);
    } else if (multipleMode) {
      removeSelect(selectItem);
    }
  }

  return (
    <Select
      popupVisible={popupVisible}
      triggerProps={{
        autoAlignPopupWidth: false,
        autoAlignPopupMinWidth: true,
        position: position,
      }}
      triggerElement={children}
      onVisibleChange={setPopupVisible}
      dropdownRender={() => {
        return options && options.length > 0 ? (
          <div className={searchStyles['search']}>
            {
              options.length > 10 && <>
                <div className={searchStyles['search-input-outer']}>
                  <Input className={searchStyles['search-input']} size={"small"} placeholder={tg['ope.search']}/>
                </div>
                <Divider type="horizontal" style={{ margin: '8px 0' }} />
              </>
            }
            <div className={listStyles['list-outer']}>
              { options.map((item, index) => (
                <div key={index} className={cs(listStyles['list-item'],
                  {[listStyles['list-item-active']]: isSelected(item)})}
                     onClick={() => {
                       onSelect(item);
                       setPopupVisible(false);
                     }}>
                  {
                    typeof item !== 'string' && <div className={listStyles['list-item-icon']}>{item.icon}</div>
                  }
                  <Typography.Text className={listStyles['list-item-name']}>
                    {typeof item !== 'string' ? item.name : item }
                  </Typography.Text>
                </div>
              ))}
            </div>
          </div>
        ) : (
          <div className={listStyles['list-empty']}>
            <div className={listStyles['list-empty-icon']}>
              <IconEmptyCustom />
            </div>
            <Typography.Text className={listStyles['list-empty-text']}>{t['list.empty']}</Typography.Text>
          </div>
        )
      }}
    />
  );
}

export default SearchSelect;
