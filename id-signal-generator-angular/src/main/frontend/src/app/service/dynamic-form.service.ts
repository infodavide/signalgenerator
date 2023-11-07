import { Injectable } from '@angular/core';
import { FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { InputBase } from '../dynamic-form';

@Injectable({
    providedIn: 'root'
})
export class DynamicFormService {

    toInputBase(obj: Object): InputBase | null {
        type ObjectKey = keyof typeof obj;
        let name: string | null = null;

        if (obj.hasOwnProperty('name') && obj.hasOwnProperty('type')) {
            return obj as InputBase;
        }

        return null;
    }

    toFormControl(input: InputBase): FormControl {
        let validators: ValidatorFn[] = [];

        if (input.required) {
            validators.push(Validators.required);
        }

        if (input.max) {
            if (input.type === 'string' || input.type === 'textbox') {
                validators.push(Validators.maxLength(input.max));
            } else {
                validators.push(Validators.max(input.max));
            }
        }

        if (input.min) {
            if (input.type === 'string' || input.type === 'textbox') {
                validators.push(Validators.minLength(input.min));
            } else {
                validators.push(Validators.min(input.min));
            }
        }

        switch (input.type) {
            case 'boolean': {
                return new FormControl<boolean>(input.value || false, validators);

                break;
            }
            case 'integer': 
            case 'double': 
            case 'number': {
                return new FormControl<number>(input.value || null, validators);

                break;
            }
            default: {
                return new FormControl<string>(input.value || null, validators);
            }
        }
    }

    appendToFormGroup(form:  FormGroup, inputs: InputBase[]): void {
        const sorted: InputBase[] = [];
        inputs.forEach(input => sorted.push(Object.assign({}, input)));
        sorted.sort((a, b) => a.order - b.order);
        sorted.forEach(input => {
            form.addControl(input.name, this.toFormControl(input));
        });
        form.updateValueAndValidity({ onlySelf: false, emitEvent: true });        
    }
}