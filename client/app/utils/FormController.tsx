/* eslint-disable react/jsx-key */
import { useState } from "react";
import { ButtonField, DefaultField } from "~/components";

interface DataProps {
  readonly formData: any[];
}

export function getFieldClassName(customClassName: string | undefined) {
  return customClassName ?? "col-xl-4 col-lg-4 col-md-4 col-sm-4 col-12";
}

export function getOptionTitle(optionData: any, data: any) {
  if (data.loadCustom) {
    return `${optionData[data?.customKeys?.optionTitle]}${optionData[data?.customKeys?.optionTitle2] ? " " + optionData[data?.customKeys?.optionTitle2] : ""}`;
  }
  return optionData.title;
}

export function isOptionSelected(optionData: any, data: any, selectOptions: any) {
  if (selectOptions) {
    if (
      selectOptions[data?.name] === optionData.value ||
      optionData[data?.customKeys?.code] === selectOptions[data?.name]
    ) {
      return true;
    }
  }
  return false;
}

export default function FormController({
  formData
}: Readonly<DataProps>) {

  return (
    <>
      {formData.map((data: any, index: number) => {


        switch (data.type) {
          case "selectSearch":
            return (
              <div></div>
              // <SelectSearchField
              //   data={data}
              //   isHidden={isHidden}
              //   isRequired={isRequired}
              //   customOptions={customOptions}
              //   selectOptions={selectOptions}
              //   selectedValue={selectedValue}
              //   entityID={entityID}
              //   setEntityID={setEntityID}
              //   setSelectedValue={setSelectedValue}
              //   onSearch={onSearch}
              //   onChange={onChange}
              //   fieldClassName={fieldClassName}
              //   optionArray={optionArray}
              // />
            );
          case "button":
          case "submit":
            return (
              <ButtonField
                key={data.text ?? index}
                {...data}
              />
            );
          default:
            return (
              <DefaultField
                key={data.name ?? index}
                {...data}
              />
            );
        }
      })}
    </>
  );
}