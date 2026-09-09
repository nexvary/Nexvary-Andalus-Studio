from .main import HealthResponse, app
from .security import router as security_router
from .stage625 import router as stage625_router
from .stage825 import router as stage825_router

# Keep one authoritative health route while preserving all earlier API contracts.
app.router.routes = [route for route in app.router.routes if getattr(route, "path", None) != "/health"]


@app.get("/health", response_model=HealthResponse)
def health_stage825() -> HealthResponse:
    return HealthResponse(status="ok", service="nexvary-andalus-ai-api", version="0.8.25")


app.include_router(stage625_router)
app.include_router(stage825_router)
app.include_router(security_router)
app.version = "0.8.25"
