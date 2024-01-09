interface Item {
  fileId: string,
  fileName: string,
  title: string,
  artist: string,
  album: string,
  year: string,
  trackNumber: string,
  updateColumns?: string[],
}
export interface MusicSaveApiParam {
  items: Item[]
}
