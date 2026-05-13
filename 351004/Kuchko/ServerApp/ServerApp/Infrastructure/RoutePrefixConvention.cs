using Microsoft.AspNetCore.Mvc.ApplicationModels;
using Microsoft.AspNetCore.Mvc.Routing;

namespace ServerApp.Infrastructure;

public class ApiPrefixConvention : IApplicationModelConvention
{
    private readonly AttributeRouteModel _routePrefix;

    public ApiPrefixConvention(IRouteTemplateProvider routeTemplateProvider)
    {
        _routePrefix = new AttributeRouteModel(routeTemplateProvider);
    }

    public void Apply(ApplicationModel application)
    {
        foreach (var selector in application.Controllers.SelectMany(c => c.Selectors))
            if (selector.AttributeRouteModel != null)
                selector.AttributeRouteModel =
                    AttributeRouteModel.CombineAttributeRouteModel(_routePrefix, selector.AttributeRouteModel);
            else
                selector.AttributeRouteModel = _routePrefix;
    }
}