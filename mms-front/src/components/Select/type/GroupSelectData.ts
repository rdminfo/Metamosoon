import {SelectItemData} from "@/components/Select/type/SelectItemData";

export interface GroupInfo {
  name: string;
  code: string
}
export interface GroupSelectData {
  groupInfo: GroupInfo;
  items: SelectItemData[];
}
