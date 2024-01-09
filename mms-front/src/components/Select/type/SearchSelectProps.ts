import {CommonSelectProps} from "@/components/Select/type/CommonSelectProps";
import {SelectItemData} from "@/components/Select/type/SelectItemData";

export interface SearchSelectProps extends CommonSelectProps{
  options: SelectItemData[] | string[];
  onChange?: (selectData: SelectItemData[] | string[]) => void;
}