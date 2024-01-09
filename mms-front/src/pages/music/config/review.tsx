export const getReviewLayoutConfig = () => {
  return {
    labelCol: {
      span: 5,
    },
    wrapperCol: {
      span: 19,
    },
  }
}

export const getReviewItemsConfig = (t: any, tg: any) => {
  return [
    {
      name: tg['music.fileName'],
      value: 'fileName',
      type: 'input',
    },
    {
      name: tg['music.songName'],
      value: 'fileName',
      type: 'input',
    },
    {
      name: tg['music.artist'],
      value: 'fileName',
      type: 'input',
    },
    {
      name: tg['music.album'],
      value: 'fileName',
      type: 'input',
    },
    {
      name: tg['music.cover'],
      value: 'cover',
      type: 'image',
    },
    {
      name: tg['music.year'],
      value: 'fileName',
      type: 'input',
    },
    {
      name: tg['music.trackNumber'],
      value: 'fileName',
      type: 'input',
    },
    {
      name: tg['music.lyric'],
      value: 'fileName',
      type: 'textArea',
    },
  ]
}
