filename = "gcp-scaled-http-10.txt"

ts_sum = tj_sum = 0

with open(filename) as f:
    content = f.readlines()
    errors = 0
    for line in content:
    	try:
    		ts, tj = line.strip().split(",")
    		ts_sum += int(ts)
    		tj_sum += int(tj)
    	except:
    		errors += 1

    denom = (len(content) - errors) * 10**6
    print("Average Search Servlet Time(ms): " + str(ts_sum / denom))
    print("Average JDBC Time(ms): " + str(tj_sum / denom))