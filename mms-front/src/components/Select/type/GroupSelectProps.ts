import {CommonSelectProps} from "@/components/Select/type/CommonSelectProps";
import {GroupSelectData} from "@/components/Select/type/GroupSelectData";

export interface GroupSelectProps extends CommonSelectProps{
  options: GroupSelectData[];
  onChange?: (selectData: GroupSelectData[]) => void;
}