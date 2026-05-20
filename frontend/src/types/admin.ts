export interface AdminTaskDto {
  id: number;
  title: string;
  groupName: string | null;
  description: string | null;
  daysToComplete: number | null;
  active: boolean;
}

export interface ParameterMeta {
  value: string;
  label: string;
}

export interface OperatorMeta {
  value: string;
  label: string;
}

export interface DisplayRuleConditionDto {
  id: number;
  parameterKey: string;
  operator: string;
  value: string | null;
}

export interface DisplayRuleGroupDto {
  ruleGroupId: number;
  taskId: number;
  conditions: DisplayRuleConditionDto[];
}

export interface DisplayRuleConditionRequest {
  parameterKey: string;
  operator: string;
  value?: string | null;
}

export interface DisplayRuleGroupRequest {
  conditions: DisplayRuleConditionRequest[];
}
