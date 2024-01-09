import {createUrl, del, get, post, put} from '@/service/api';
import { MusicListApiParam } from '@/pages/music/types/MusicListApiParam';
import {FileMoveApiParam} from "@/pages/music/types/FileMoveApiParam";
import {MusicSaveApiParam} from "@/pages/music/types/MusicSaveApiParam";

export function fetchFolderTreeList() {
  return get(createUrl('/files/folders/tree/music'));
}

export function fetchFolderParentList(_, folderId) {
  return get(createUrl(`/files/folders/parents/${folderId}`));
}

export function fetchMusicList(_, { arg }: { arg: MusicListApiParam }) {
  const { folderId, ...params } = arg;
  return get(createUrl(`/music/list/${folderId}`), params);
}

export function fetchMusicDetail(fileId: string) {
  return get(createUrl(`/music/${fileId}`));
}

export function moveFile(data: FileMoveApiParam) {
  return put(createUrl('/files/move'), data);
}

export function deleteFile(fileIds: string) {
  return del(createUrl(`/files/${fileIds}`));
}

export function zipFile(fileIds: string) {
  return post(createUrl(`/files/zip/${fileIds}`));
}

export function saveFolder(parentFileId: string, folderNames: string) {
  return post(createUrl(`/files/folders/${parentFileId}/${folderNames}`));
}

export function updFolder(fileIds: string, folderNames: string) {
  return put(createUrl(`/files/folders/${fileIds}/${folderNames}`));
}

export function musicSave(data: MusicSaveApiParam) {
  return put(createUrl('/music'), data);
}

export function delMusicCover(fileId: string) {
  return del(createUrl(`/music/cover/${fileId}`));
}

export function updMusicLyrics(fileId: string, lyrics) {
  return put(createUrl(`/music/lyrics/${fileId}`), { lyrics });
}