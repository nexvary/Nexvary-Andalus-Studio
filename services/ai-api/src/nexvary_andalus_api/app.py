from .main import HealthResponse, app
from .stage625 import router as stage625_router

# Replace the Stage-425 health route so the public version is not stale.
app.router.routes = [route for route in app.router.routes if getattr(route, "path", None) != "/health"]


@app.get("/health", response_model=HealthResponse)
def health_stage625() -> HealthResponse:
    return HealthResponse(status="ok", service="nexvary-andalus-ai-api", version="0.6.25")


app.include_router(stage625_router)
app.version = "0.6.25"
