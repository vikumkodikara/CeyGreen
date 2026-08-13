import React from 'react';
import { OrderStatus } from '../../types/order';

const STEPS: OrderStatus[] = ['PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED'];

interface Props {
  status: OrderStatus;
}

export const OrderStatusTimeline: React.FC<Props> = ({ status }) => {
  if (status === 'CANCELLED') {
    return (
      <div className="order-timeline">
        <div className="order-step done">Pending</div>
        <div className="order-step cancelled">Cancelled</div>
      </div>
    );
  }

  const currentIndex = STEPS.indexOf(status);

  return (
    <div className="order-timeline">
      {STEPS.map((step, index) => {
        const label = step.charAt(0) + step.slice(1).toLowerCase();
        const done = index <= currentIndex;
        const active = index === currentIndex;
        return (
          <div key={step} className={`order-step ${done ? 'done' : ''} ${active ? 'active' : ''}`}>
            <span className="order-step-dot" />
            <span>{label}</span>
          </div>
        );
      })}
    </div>
  );
};
