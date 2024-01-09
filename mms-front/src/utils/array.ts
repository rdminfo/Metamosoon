/**
 * 比较两个数组数据是否相等
 */
export function arraysEqual<T>(
  arr1: T[],
  arr2: T[],
  excludedFields: (keyof T)[] = []
): boolean {

  if (!arr1 || !arr2 || arr1.length !== arr2.length) {
    return false;
  }

  for (let i = 0; i < arr1.length; i++) {
    const obj1 = arr1[i];
    const obj2 = arr2[i];

    for (const key in obj1) {
      if (!excludedFields.includes(key as keyof T)) {
        if (JSON.stringify(obj1[key]) !== JSON.stringify(obj2[key])) {
          return false;
        }
      }
    }
  }

  return true;
}
