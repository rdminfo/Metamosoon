import { FileSelectListItem } from '@/pages/music/types/FileSelectListItem';

export interface FileSelectList {
  folderId: string;
  folderName: string;
  selectFolderCount?: number;
  selectFileCount?: number;
  selectCount?: number;
  items?: FileSelectListItem[];
}
