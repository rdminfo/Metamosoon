import React from "react";
import {
  IconArrowDown,
  IconArrowUp, IconClockCircle, IconCodeBlock, IconDragDot, IconDriveFile, IconFile, IconFileAudio,
  IconFolder,
  IconNav, IconOrderedList,
  IconQuote,
  IconSchedule,
  IconUser
} from "@arco-design/web-react/icon";
import {GroupSelectData} from "@/components/Select/type/GroupSelectData";
import {FileType, Sort, SortCategory, SortOrder, TagRange, ViewType} from "@/pages/music/enums/MusicListCondition";

export const getViewTypeConfig = (tg): GroupSelectData[] => [
  {
    groupInfo: {
      name: tg['music.content.top.viewType.tagRange'],
      code: ViewType.TAG_RANGE,
    },
    items: [
      {
        name: tg['music.content.top.viewType.tagRange.basic'],
        value: TagRange.BASIC,
        icon: <IconFileAudio />
      },
      {
        name: tg['music.content.top.viewType.tagRange.other'],
        value: TagRange.OTHER,
        icon: <IconDragDot />
      }
    ],
  },
  {
    groupInfo: {
      name: tg['music.content.top.viewType.fileType'],
      code: ViewType.FILE_TYPE,
    },
    items: [
      {
        name: tg['music.content.top.viewType.fileType.default'],
        value: FileType.DEFAULT,
        icon: <IconFile />
      },
      {
        name: tg['music.content.top.viewType.fileType.onlyFolder'],
        value: FileType.ONLY_FOLDER,
        icon: <IconFolder />
      },
      {
        name: tg['music.content.top.viewType.fileType.onlyMusic'],
        value: FileType.ONLY_MUSIC,
        icon: <IconFileAudio />
      },
      {
        name: tg['music.content.top.viewType.fileType.all'],
        value: FileType.ALL,
        icon: <IconDriveFile />
      },
    ],
  },
]

export const getSortTypeConfig = (t, tg): GroupSelectData[] => [
  {
    groupInfo: {
      name: tg['sort.category'],
      code: Sort.CATEGORY,
    },
    items: [
      {
        name: tg['music.fileName'],
        value: SortCategory.FILE_NAME,
        icon: <IconFolder />
      },
      {
        name: tg['music.songName'],
        value: SortCategory.SONG_NAME,
        icon: <IconQuote />
      },
      {
        name: tg['music.artist'],
        value: SortCategory.ARTIST,
        icon: <IconUser />
      },
      {
        name: tg['music.album'],
        value: SortCategory.ALBUM,
        icon: <IconNav />
      },
      {
        name: tg['music.trackNumber'],
        value: SortCategory.TRACK_NUMBER,
        icon: <IconOrderedList />
      },
      {
        name: tg['music.year'],
        value: SortCategory.YEAR,
        icon: <IconClockCircle />
      },
      {
        name: tg['music.size'],
        value: SortCategory.SIZE,
        icon: <IconCodeBlock />
      },
      {
        name: tg['music.trackLength'],
        value: SortCategory.TRACK_LENGTH,
        icon: <IconSchedule />
      },
    ],
  },
  {
    groupInfo: {
      name: tg['sort.order'],
      code: Sort.ORDER,
    },
    items: [
      {
        name: tg['sort.order.asc'],
        value: SortOrder.ASC,
        icon: <IconArrowUp />
      },
      {
        name: tg['sort.order.desc'],
        value: SortOrder.DESC,
        icon: <IconArrowDown />
      },
    ],
  }
]

