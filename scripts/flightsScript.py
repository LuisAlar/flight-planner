import pandas as pd 

INPUT_FILE = "./KaggleFlights.csv"
OUTPUT_FILE = "fl.txt"

needed_cols = ["Year", "city1", "city2", "nsmiles", "fare"]
chunksize = 100000
filtered_chunks = []

# tune these numbers downward if you still run out of memory
NUM_ORIGINS = 10              # max number of origin cities to keep
MAX_FLIGHTS_PER_ORIGIN = 5    # max number of flights FROM each origin

# 1) Read in chunks, filter to 2024, and keep needed columns
for chunk in pd.read_csv(INPUT_FILE, chunksize=chunksize, low_memory=False):
    chunk["Year"] = pd.to_numeric(chunk["Year"], errors="coerce")
    chunk_2024 = chunk[chunk["Year"] == 2024]
    chunk_2024 = chunk_2024[needed_cols]
    filtered_chunks.append(chunk_2024)

df_2024 = pd.concat(filtered_chunks, ignore_index=True)

# 2) Drop duplicate routes (same city1 -> city2)
df_unique = df_2024.drop_duplicates(subset=["city1", "city2"], keep="first")

# 3) Limit flights per origin
#    group by origin, and keep only the first MAX_FLIGHTS_PER_ORIGIN flights
df_limited_per_origin = (
    df_unique
    .sort_values(["city1", "city2"])  # just for stable ordering
    .groupby("city1", as_index=False)
    .head(MAX_FLIGHTS_PER_ORIGIN)
)

# 4) Now limit the number of origins
unique_origins = df_limited_per_origin["city1"].dropna().unique()
selected_origins = unique_origins[:NUM_ORIGINS]

df_limited = df_limited_per_origin[
    df_limited_per_origin["city1"].isin(selected_origins)
].copy()

# 5) Drop Year and clean numeric fields
df_routes = df_limited[["city1", "city2", "nsmiles", "fare"]].copy()
df_routes["nsmiles"] = pd.to_numeric(df_routes["nsmiles"], errors="coerce")
df_routes["fare"] = pd.to_numeric(df_routes["fare"], errors="coerce")
df_routes = df_routes.dropna(subset=["nsmiles", "fare"])

print("Number of origins:", len(selected_origins))
print("Number of flights (rows):", len(df_routes))

# 6) Write in your custom format for Java
with open(OUTPUT_FILE, "w", encoding="utf-8") as f:
    f.write(str(len(df_routes)) + "\n")

    for _, row in df_routes.iterrows():
        origin = str(row["city1"]).strip().strip('"')
        dest   = str(row["city2"]).strip().strip('"')

        miles = int(row["nsmiles"])
        fare_int = int(round(float(row["fare"])))

        line = f"{origin}|{dest}|{miles}|{fare_int}\n"
        f.write(line)

print("Done! Wrote flights in Java format to", OUTPUT_FILE)
