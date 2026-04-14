export const createFieldsUrlParams = function (fields) {
  const fieldsParams = fields ? "fields=" + fields.join(",") : "";
  return fieldsParams;
};

export const createPageableUrlParams = function (pagination) {
  if (!pagination) return "";

  const { sortBy, descending, page, rowsPerPage } = pagination;
  const sortDirection = descending ? "desc" : "asc";
  const pageableParams = new URLSearchParams({
    page: page - 1,
    size: rowsPerPage
  });

  if (sortBy) {
    pageableParams.append("sort", `${sortBy},${sortDirection}`);
  }

  return pageableParams.toString();
};

export const createFilterParams = (filter) => {
  if (!filter) return "";

  const params = new URLSearchParams();

  Object.keys(filter).forEach((val) => {
    const value = filter[val];
    if (value === null || value === undefined) {
      return;
    }
    params.append(val, value);
  });

  return params.toString();
};
