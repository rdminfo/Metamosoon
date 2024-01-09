interface AggregateConditionItem {
  value: string;
  badgeCount: number[];
}
export default interface AggregateConditionState {
  organized?: AggregateConditionItem;
  album?: AggregateConditionItem;
  cover?: AggregateConditionItem;
  lyric?: AggregateConditionItem;
  year?: AggregateConditionItem;
}
