export async function createEstimateRequest(purpose, budget, otherRequest) {
    const response = await fetch("http://localhost:8080/estimate/request", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        // 쿠키를 자동으로 포함시키기 위해 credentials 옵션 추가
        credentials: 'include',
        body: JSON.stringify({ purpose, budget, otherRequest })
    });

    if (!response.ok) {
        throw new Error("Failed to create estimate request");
    }
}