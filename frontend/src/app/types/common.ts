export interface UserInfo {
  username: string;
  customer_name: string;
  email: string;
}

export interface Quote {
  id: number;
  createDate: string;
  budget: string;
  purpose: string;
  status: string;
  parts?: {
    cpu: string;
    motherboard: string;
    memory: string;
    storage: string;
    gpu: string;
    case: string;
    power: string;
  };
}

export interface Comment {
  id: number;
  author: string;
  text: string;
  date: string;
  isCustomer: boolean;
} 