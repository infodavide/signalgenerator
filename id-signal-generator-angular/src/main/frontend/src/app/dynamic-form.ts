
export class InputBase {
    value: any | undefined;
    name: string;
    description: string | undefined;
    label: string;
    required: boolean = false;
    max: number | undefined;
    min: number | undefined;
    order: number = 0;
    type: string = 'string';
    options: { key: string, value: string }[];

    constructor(options: {
        value?: any;
        name?: string;
        description?: string;
        label?: string;
        required?: boolean;
        max?: number;
        min?: number;
        order?: number;
        type?: string;
        options?: { key: string, value: string }[];
    } = {}) {
        this.value = options.value;
        this.name = options.name || '';
        this.description = options.description || undefined;
        this.label = options.label || '';
        this.required = !!options.required;
        this.max = options.max || undefined;
        this.min = options.min || undefined;
        this.order = options.order === undefined ? 1 : options.order;
        this.type = options.type || '';        
        this.options = options.options || [];
    }
}
