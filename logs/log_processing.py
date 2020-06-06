filename = "single-instance-http-10-nopooling.txt"

ts_sum = tj_sum = 0

with open(filename) as f:
    content = f.readlines()
    for line in content:
    	ts, tj = line.strip().split(",")
    	ts_sum += int(ts)
    	tj_sum += int(tj)

    denom = len(content) * 10**6
    print("Average Search Servlet Time(ms): " + str(ts_sum / denom))
    print("Average JDBC Time(ms): " + str(tj_sum / denom))