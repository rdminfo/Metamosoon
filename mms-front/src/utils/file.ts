export function isMusicFileType(fileType: string): boolean {
  const musicFileExtensions: string[] = ['mp3', 'flac', 'wav', 'aac', 'ogg']; // 音乐文件的常见扩展名列表
  const extension = fileType.toLowerCase();
  return musicFileExtensions.includes(extension);
}


export function formatFileSize(bytes) {
  if (bytes === 0) return '0';

  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));

  return `${parseFloat((bytes / Math.pow(k, i)).toFixed(2))} ${sizes[i]}`;
}
