export const getConditionsConfig = (t: any) => {
  return {
    card : [
      {
        name: 'organized',
        title: t['music.aggregate.condition.organized'],
        data: [
          {
            name: t['music.aggregate.condition.organized.organized'],
            value: 'organized',
          },
          {
            name: t['music.aggregate.condition.organized.disorganized'],
            value: 'disorganized',
          }
        ],
      },
      {
        name: 'album',
        title: t['music.aggregate.condition.album'],
        data: [
          {
            name: t['music.aggregate.condition.album.has'],
            value: 'has',
          },
          {
            name: t['music.aggregate.condition.album.no'],
            value: 'no',
          }
        ]
      },
      {
        name: 'cover',
        title: t['music.aggregate.condition.cover'],
        data: [
          {
            name: t['music.aggregate.condition.cover.has'],
            value: 'has',
          },
          {
            name: t['music.aggregate.condition.cover.no'],
            value: 'no',
          }
        ],
      },
      {
        name: 'lyric',
        title: t['music.aggregate.condition.lyric'],
        data: [
          {
            name: t['music.aggregate.condition.lyric.has'],
            value: 'has',
          },
          {
            name: t['music.aggregate.condition.lyric.no'],
            value: 'no',
          }
        ],
      },
      {
        name: 'year',
        title: t['music.aggregate.condition.year'],
        data: [
          {
            name: t['music.aggregate.condition.year.has'],
            value: 'has',
          },
          {
            name: t['music.aggregate.condition.year.no'],
            value: 'no',
          }
        ]
      },
    ],
    popover : [
      {
        name: 'organized',
        title: t['music.aggregate.condition.organized'],
        data: [
          {
            name: t['music.aggregate.condition.organized.organized'],
            value: 'organized',
          },
          {
            name: t['music.aggregate.condition.organized.disorganized'],
            value: 'disorganized',
          }
        ],
      },
      {
        name: 'organized',
        title: t['music.aggregate.condition.organized'],
        data: [
          {
            name: t['music.aggregate.condition.organized.organized'],
            value: 'organized',
          },
          {
            name: t['music.aggregate.condition.organized.disorganized'],
            value: 'disorganized',
          }
        ],
      },
    ],
  }
}
