import {FolderListItem} from "@/pages/music/types/FolderListItem";

export interface FolderTreeListItem extends FolderListItem{
  children: FolderTreeListItem[];
}
