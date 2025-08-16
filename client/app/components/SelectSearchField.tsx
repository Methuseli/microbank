import { Form, Select, Spin } from "antd";
import { getOptionTitle } from "~/utils/FormController";

interface SelectSearchFieldProps {
    data: any;
    isHidden: boolean;
    isRequired: boolean;
    customOptions: any;
    selectOptions: any;
    selectedValue: any;
    entityID: any;
    setEntityID: any;
    setSelectedValue: any;
    onSearch?: Function;
    onChange: any;
    fieldClassName?: string;
    optionArray: any;
}

const SelectSearchField = (props: SelectSearchFieldProps) => {
    const {
        data,
        isHidden,
        isRequired,
        customOptions,
        selectOptions,
        selectedValue,
        entityID,
        setEntityID,
        setSelectedValue,
        onSearch,
        onChange,
        fieldClassName,
        optionArray
    } = props;

    return (
        <div
            className={fieldClassName}
            key={data?.name}
            hidden={isHidden}
        >
            <Form.Item
                hidden={data.isHidden ? isHidden : false}
                name={data.name}
                rules={[
                    {
                        required: isRequired === false ? data.required : isRequired,
                        message: "Please enter this field"
                    }
                ]}
            >
                <Spin spinning={optionArray?.length <= 0}>
                    {selectOptions?.[data?.name]
                        ? <Select
                            value={selectedValue[data?.name] && entityID === selectOptions["id"] ? selectedValue[data?.name] : selectOptions[data?.name]}
                            style={{ marginTop: "12px" }}
                            showSearch
                            optionFilterProp="children"
                            placeholder={`Search ${data.title}`}
                            onSearch={(search) => {
                                onSearch?.(search, data.name);
                            }}
                            onChange={(search) => {
                                const source = data?.name;
                                setEntityID(selectOptions["id"]);
                                setSelectedValue((prevSelectedValue: any) => {
                                    return {
                                        ...prevSelectedValue,
                                        [source]: search,
                                    }
                                });
                                onChange(search, data.name);
                            }}
                            filterOption={(input, option) =>
                                (option?.value ?? '').toString().toLowerCase().includes(input.toLowerCase())
                            }
                        >
                            {customOptions[data?.name]?.map((optionData: any) => (
                                <Select.Option
                                    key={
                                        data.loadCustom
                                            ? optionData[data?.customKeys?.code]
                                            : optionData.value
                                    }
                                    value={
                                        data.loadCustom
                                            ? optionData[data?.customKeys?.code]
                                            : optionData.value
                                    }
                                >
                                    {getOptionTitle(optionData, data)}
                                </Select.Option>
                            ))}
                        </Select>
                        : <Select
                            style={{ marginTop: "12px" }}
                            showSearch
                            optionFilterProp="children"
                            placeholder={`Search ${data.title}`}
                            onSearch={(search) => {
                                onSearch?.(search, data.name);
                            }}
                            onChange={(search) => {
                                onChange(search, data.name);
                            }}
                            filterOption={(input, option) =>
                                (option?.value ?? '').toString().toLowerCase().includes(input.toLowerCase())
                            }
                        >
                            {customOptions[data?.name]?.map((optionData: any) => (
                                <Select.Option
                                    key={
                                        data.loadCustom
                                            ? optionData[data?.customKeys?.code]
                                            : optionData.value
                                    }
                                    value={
                                        data.loadCustom
                                            ? optionData[data?.customKeys?.code]
                                            : optionData.value
                                    }
                                >
                                    {getOptionTitle(optionData, data)}
                                </Select.Option>
                            ))}
                        </Select>
                    }
                </Spin>
            </Form.Item>
        </div>
    );
}

export default SelectSearchField;
