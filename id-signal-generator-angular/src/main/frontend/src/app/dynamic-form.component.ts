import { Component, Input, ViewEncapsulation } from '@angular/core';
import { InputBase } from './dynamic-form';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'app-dynamic-form-component',
  templateUrl: './dynamic-form.component.html',
  styleUrls: ['./dynamic-form.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class DynamicFormComponent {
  @Input() input!: InputBase;
  @Input() form!: FormGroup;

  get isValid(): boolean { 
    if (this.form) {
      let control = this.form.controls[this.input.name];

      return control && control.valid;
    }

    return false; 
  }

  getErrorMessage(): string | null {
    if (this.form) {
      let control = this.form.controls[this.input.name];

      if (control != null && control.invalid && control.errors) {
          if (control.errors['reason']) {
              return control.errors['reason'];
          }

          if (control.errors['message']) {
              return control.errors['message'];
          }

          return 'Not valid';
      }
    }

    return null;
  }
}
