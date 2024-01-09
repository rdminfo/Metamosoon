import {ReactNode} from "react";

export interface CommonSelectProps {
  mode?: 'single' | 'multiple';
  position?: 'top' | 'tl' | 'tr' | 'bottom' | 'bl' | 'br' | 'left' | 'lt' | 'lb' | 'right' | 'rt' | 'rb';
  resetSingle?: number;
  children: ReactNode;
}