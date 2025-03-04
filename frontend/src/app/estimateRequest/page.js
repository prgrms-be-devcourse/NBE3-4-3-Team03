import EstimateForm from "./components/form";

export default function EstimatePage() {
    return (
        <div className="max-w-md mx-auto mt-10">
            <h1 className="text-xl font-bold">PC 견적 요청</h1>
            <EstimateForm />
        </div>
    );
}
