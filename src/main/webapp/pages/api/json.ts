import { pool } from "../../dbconfig/dbconfig";
import { NextApiRequest, NextApiResponse } from "next";

export default async (req: NextApiRequest, res: NextApiResponse) => {

    const [
        rows,
    ] = await pool.query(
        `SELECT json_graph FROM results WHERE repository_name = ?`,
        [req.body.name]
    );
    res.setHeader("content-type", "text/html; charset=utf-8");
    res.status(200).json(rows.length > 0 ? rows[0].json_graph : null);
};
