import React, {useEffect, useRef} from "react";
import {Card} from "@arco-design/web-react";

const ResizableCard = (props) => {
  const { onHeightChange, onWidthChange, children, ...otherProps } = props
  const ref = useRef(null);

  useEffect(() => {
    const resizeChange = (e) => {
      if (onHeightChange) {
        onHeightChange(e.contentRect.height);
      }
      if (onWidthChange) {
        onWidthChange(e.contentRect.width);
      }
      // console.log(e.contentRect.height, e, '监听元素正在发生变化!');
    };

    const observedElement = ref.current; // Store ref.current in a variable

    const observer = new ResizeObserver((entries) => {
      for (const entry of entries) {
        resizeChange(entry);
      }
    });

    if (observedElement) {
      observer.observe(observedElement);
    }

    return () => {
      if (observedElement) {
        observer.unobserve(observedElement);
      }
    };
  }, [onHeightChange]);

  return <Card {...otherProps} ref={ref}>{children}</Card>;
};

export default ResizableCard;
